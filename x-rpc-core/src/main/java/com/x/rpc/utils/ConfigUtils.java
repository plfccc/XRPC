package com.x.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lingpfeng
 * @date 2025/04/27 20:58:47
 * @description 配置工具类
 */
public class ConfigUtils {
    public static <T> T loadConfigYaml(Class<T> tClass, String suffix) {
        return loadConfigYaml(tClass, suffix, "");
    }

    private static <T> T loadConfigYaml(Class<T> tClass, String suffix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(suffix);
        //解析类
        Yaml yaml = new Yaml(new Constructor(tClass,new LoaderOptions()));
        try (InputStream in = ConfigUtils.class
                .getClassLoader()
                .getResourceAsStream(configFileBuilder.toString())) {
            if (in == null) {
                throw new RuntimeException("Loading.yml/yaml config file fail!!");
            }
            return yaml.load(in);
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Loading .yml/yaml config file fail!! - "+e);
        }
    }


    /**
     * 加载properties配置
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }
    /**
     * 加载properties配置(区分配置环境)
     * @param tClass
     * @param prefix
     * @param env
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        // 自动加载配置文件
        props.autoLoad(true);
        return props.toBean(tClass, prefix);
    }
}
