package com.x.rpc.serializer;

import com.x.rpc.spi.SpiLoader;

/**
 * @author lingpfeng
 * @date 2025/04/28 09:56:02
 * @description 序列化器工厂 (用于获取序列化器对象)
 */
public class SerializerFactory {

    //    /**
//     * 序列化映射（用于实现单例）
//     */
//    private static final Map<String,Serializer> KEY_SERIALIZER_MAP = Map.of(
//            SerializerKeys.JDK,new JdkSerializer(),
//            SerializerKeys.JSON,new JsonSerializer(),
//            SerializerKeys.HESSIAN,new HessianSerializer(),
//            SerializerKeys.KRYO,new KryoSerializer()
//    );
//
//    /**
//     * 默认的序列化器
//     */
//    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
//
//    /**
//     * 获取序列化器
//     * @param key
//     * @return
//     */
//    public static Serializer getInstance(String key) {
//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
//    }
    static {
        SpiLoader.load(Serializer.class);
    }
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
