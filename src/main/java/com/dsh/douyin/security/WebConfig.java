package com.dsh.douyin.security;

import com.dsh.douyin.config.DouyinProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 API-Key 鉴权拦截器，并排除 OAuth 回调与消息推送端点。
 *
 * <p>同时开放 /api/** 的跨域（CORS），便于将接口测试页面（static/douyin-api-test.html）
 * 独立托管或本地以 file:// 打开时也能调用接口。默认允许任意来源、任意请求头，
 * 且不携带凭据（API-Key 走请求头而非 Cookie），属于本地联调用的最小化配置。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final DouyinProperties props;

    public WebConfig(DouyinProperties props) {
        this.props = props;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiKeyAuthInterceptor(props))
                .addPathPatterns("/api/douyin/**")
                .excludePathPatterns(
                        "/api/douyin/auth/**",     // 平台 OAuth 回跳，无需 API-Key
                        "/api/douyin/push/**"      // 消息推送，由消息签名校验保护
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
