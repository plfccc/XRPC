package com.x.rpc.fault.tolerant;

import com.x.rpc.spi.SpiLoader;

/**
 * @author lingpfeng
 * @date 2025/05/06 17:47:34
 * @description TODO
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_RETRY_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
