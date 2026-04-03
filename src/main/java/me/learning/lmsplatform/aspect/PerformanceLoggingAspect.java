package me.learning.lmsplatform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    @Around("execution(* me.learning.lmsplatform.service.*.*(..)) || execution(* me.learning.lmsplatform.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Throwable thrownException = null;
        
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            thrownException = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (thrownException != null) {
                log.error("PERFORMANCE method={} executionTime={}ms FAILED error={}", 
                         methodName, executionTime, thrownException.getMessage());
            } else {
                log.info("PERFORMANCE method={} executionTime={}ms", methodName, executionTime);
                
                if (executionTime > 1000) {
                    log.warn("SLOW_EXECUTION method={} executionTime={}ms", methodName, executionTime);
                }
            }
        }
    }
}
