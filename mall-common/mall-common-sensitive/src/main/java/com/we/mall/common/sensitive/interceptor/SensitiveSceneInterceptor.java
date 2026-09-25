package com.we.mall.common.sensitive.interceptor;

import com.we.mall.common.sensitive.annotation.SensitiveScene;
import com.we.mall.common.sensitive.context.SensitiveSceneContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 脱敏场景拦截器
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public class SensitiveSceneInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            SensitiveScene annotation = handlerMethod.getMethodAnnotation(SensitiveScene.class);
            if (annotation != null) {
                SensitiveSceneContext.set(annotation.value());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SensitiveSceneContext.clear();
    }
}
