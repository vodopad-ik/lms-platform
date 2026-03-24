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

    @Around("execution(* me.learning.lmsplatform.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("PERFORMANCE method={} executionTime={}ms", methodName, executionTime);
            
            if (executionTime > 1000) {
                log.warn("SLOW_EXECUTION method={} executionTime={}ms", methodName, executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("PERFORMANCE_ERROR method={} executionTime={}ms error={}", 
                     methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}
