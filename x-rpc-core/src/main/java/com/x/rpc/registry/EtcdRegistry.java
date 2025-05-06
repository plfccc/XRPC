package com.x.rpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.x.rpc.config.RegistryConfig;
import com.x.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;


    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存注册中心服务缓存（只支持单个服务缓存，已废弃，请使用下方的 RegistryServiceMultiCache）
     */
    @Deprecated
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 注册中心服务缓存注册中心服务缓存（支持多个服务缓存）
     */
    private final RegistryServiceMultiCache registryServiceMultiCache = new RegistryServiceMultiCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();




    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        // 启动心跳检测
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对和租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        // 记录当前节点的注册信息
        localRegisterNodeKeySet.add(registerKey);
    }


    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        // 移除当前节点的注册信息
        localRegisterNodeKeySet.remove(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());
    }


    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中获取信息
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceMultiCache.readCache(serviceKey);
        if (serviceMetaInfoList != null) {
            return serviceMetaInfoList;
        }
        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfos = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // 加入缓存
            registryServiceMultiCache.writeCache(serviceKey,serviceMetaInfos);
            return serviceMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }


    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        localRegisterNodeKeySet.forEach(registerKey -> {
            try {
                kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(registerKey+"下线失败", e);
            }
        });
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String registerKey : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get().getKvs();
                    if (keyValues.isEmpty()) {
                        continue;
                    }
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                }catch (Exception e) {
                    throw new RuntimeException(registerKey+"续签失败", e);
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未监听,开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceMultiCache.clearCache(serviceNodeKey);
                            break;
                        case PUT:  // 这里如果有新的服务进来还是得刷新缓存
                            // 刷新注册服务缓存
                            registryServiceMultiCache.clearCache(serviceNodeKey);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

}
