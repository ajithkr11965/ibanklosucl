package com.sib.ibanklosucl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionTimeoutInterceptor sessionTimeoutInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionTimeoutInterceptor);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Custom JS and CSS - No cache
        registry.addResourceHandler("/static/assets/js/custom/**")
                .addResourceLocations("classpath:/static/assets/js/custom/")
                .setCacheControl(CacheControl.noCache()
                        .mustRevalidate());

        registry.addResourceHandler("/static/assets/css/custom/**")
                .addResourceLocations("classpath:/static/assets/css/custom/")
                .setCacheControl(CacheControl.noCache()
                        .mustRevalidate());

        // All other static content - 30 days cache
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS)
                        .cachePublic()
                        .mustRevalidate());
    }

}
