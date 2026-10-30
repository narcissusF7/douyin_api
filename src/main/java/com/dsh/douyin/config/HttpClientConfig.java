package com.dsh.douyin.config;

import com.dsh.douyin.common.log.LoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * HTTP 客户端配置：统一超时、请求日志拦截器。
 * Spring Boot 2.7（JDK 8）使用 RestTemplate，而非 Spring 6.1 才引入的 RestClient。
 */
@Configuration
public class HttpClientConfig {

    @Bean(name = "douyinRestTemplate")
    public RestTemplate douyinRestTemplate(DouyinProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.getConnectTimeout());
        factory.setReadTimeout(props.getReadTimeout());

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
        return restTemplate;
    }
}
