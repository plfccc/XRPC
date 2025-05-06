package com.x.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.x.rpc.registry.Registry;
import com.x.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author lingpfeng
 * @date 2025/04/28 11:07:36
 * @description spi加载器
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已经加载的类: 接口名=>(key=>实现类)
     */
    private static Map<String,Map<String,Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象示例缓存  类路径=>实例,单例模式
     */
    private static Map<String,Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统spi目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 自定义spi目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描目录
     */
    private static final String[] SCAN_DIRS = {RPC_SYSTEM_SPI_DIR,RPC_CUSTOM_SPI_DIR};

    /**
     * 加载类列表
     */
    private static final List<Class<?>> LOAD_CALSS_LIST = Arrays.asList(Serializer.class, Registry.class);

    /**
     * 加载所有
     */
    public static void loadAll(){
        log.info("开始加载spi");
        for (Class<?> aClass : LOAD_CALSS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> tClass,String key){
        String tClassName = tClass.getName();
        Map<String,Class<?>> classMap = loaderMap.get(tClassName);
        if (classMap == null){
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if(!classMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型的 %s 实现类", tClassName,key));
        }
        // 得到要加载的实现类型
        Class<?> implClass = classMap.get(key);
        // 从缓存中获取实例
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName,implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);

    }


    /**
     * 加载
     * @param loadClass
     */
    public static Map<String,Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为:{}的SPI",loadClass.getName());
        //扫描路径,用户自定义的spi优先级高于系统spi
        Map<String,Class<?>> keyClassMap = new ConcurrentHashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] keyAndImplClass = line.split("=");
                        if (keyAndImplClass.length != 2) {
                            continue;
                        }
                        String key = keyAndImplClass[0].trim();
                        String implClassName = keyAndImplClass[1].trim();
                        keyClassMap.put(key, Class.forName(implClassName));
                    }
                } catch (Exception e) {
                    log.error("加载类型为:{}的SPI失败",loadClass.getName(),e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;
    }

}
