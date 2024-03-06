package org.example.quantumblog.config;

import org.example.quantumblog.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiaol
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 控制器配置
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 登录页面
        registry.addViewController("/login").setViewName("login");
        // 注册页面
        registry.addViewController("/register").setViewName("register");
        // 首页
        registry.addViewController("/index").setViewName("index");
    }

    // 重写addInterceptors方法
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new LoginInterceptor())
                // 拦截所有请求
                .addPathPatterns("/**")
                // 放行登录和注册请求
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/image/**",
                        "templates/login.html",
                        "templates/register.html");
    }

}
