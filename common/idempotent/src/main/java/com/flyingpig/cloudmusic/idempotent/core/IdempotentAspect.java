package com.flyingpig.cloudmusic.idempotent.core;

import com.flyingpig.cloudmusic.idempotent.annotation.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 幂等注解 AOP 拦截器
 */
@Aspect
public final class IdempotentAspect {

    /**
     * 增强方法标记
     */
    @Around("@annotation(com.flyingpig.cloudmusic.idempotent.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取幂等注解
        Idempotent idempotent = getIdempotent(joinPoint);
        // 根据幂等注解的场景(http or mq)和实现类型(三种实现类型)获取对应实例
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        Object resultObj;
        try {
            // 执行实例
            instance.execute(joinPoint, idempotent);
            // 执行原有方法流程
            resultObj = joinPoint.proceed();
            // 实例后置处理
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            /*
             * 消费队列幂等消费异常
             * 触发幂等逻辑时可能有两种情况：
             * 1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 RocketMQ 再次通过重试队列投递
             * 2. 消息处理成功了，该消息直接返回成功即可
             */
            if (!ex.getError()) {
                return null;
            }
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
