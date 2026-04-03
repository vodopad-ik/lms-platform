package me.learning.lmsplatform.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "performanceStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            if (ex != null || status >= 400) {
                log.error("HTTP_PERFORMANCE method={} uri={} status={} executionTime={}ms FAILED error={}",
                         method, uri, status, executionTime, ex != null ? ex.getMessage() : "N/A");
            } else {
                log.info("HTTP_PERFORMANCE method={} uri={} status={} executionTime={}ms",
                        method, uri, status, executionTime);
            }
        }
    }
}
