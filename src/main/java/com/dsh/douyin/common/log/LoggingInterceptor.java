package com.dsh.douyin.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 出站 HTTP 请求日志拦截器：记录方法、URL、响应状态码与耗时（不记录密钥/令牌内容，也不消费响应体）。
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        long start = System.currentTimeMillis();
        String uri = request.getURI().toString();
        log.info("[DOUYIN-OUT] {} {}", request.getMethod(), uri);
        ClientHttpResponse response = execution.execute(request, body);
        long cost = System.currentTimeMillis() - start;
        int status = response.getStatusCode().value();
        if (status >= 400) {
            log.warn("[DOUYIN-OUT] 状态码={} 耗时={}ms", status, cost);
        } else {
            log.info("[DOUYIN-OUT] 状态码={} 耗时={}ms", status, cost);
        }
        return response;
    }
}
