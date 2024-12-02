package com.flyingpig.cloudmusic.idempotent.core;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 幂等上下文
 */
public final class IdempotentContext {
    
    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();
    
    public static Map<String, Object> get() {
        return CONTEXT.get();
    }
    
    public static Object getKey(String key) {
        Map<String, Object> context = get();
        if (CollUtil.isNotEmpty(context)) {
            return context.get(key);
        }
        return null;
    }
    
    public static String getString(String key) {
        Object actual = getKey(key);
        if (actual != null) {
            return actual.toString();
        }
        return null;
    }
    
    public static void put(String key, Object val) {
        // 获得上下文
        Map<String, Object> context = get();
        if (CollUtil.isEmpty(context)) {
            // 如果为空，创建一个新的上下文
            context = Maps.newHashMap();
        }
        // 锁
        context.put(key, val);
        putContext(context);
    }

    public static void putContext(Map<String, Object> context) {
        // 获得上下文
        Map<String, Object> threadContext = CONTEXT.get();
        if (CollUtil.isNotEmpty(threadContext)) {
            // 如果不为空，放入所有的值
            threadContext.putAll(context);
            return;
        }
        // 设置上下文
        CONTEXT.set(context);
    }
    
    public static void clean() {
        CONTEXT.remove();
    }
}
