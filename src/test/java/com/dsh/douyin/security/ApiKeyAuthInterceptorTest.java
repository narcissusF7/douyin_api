package com.dsh.douyin.security;

import com.dsh.douyin.config.DouyinProperties;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * API-Key 鉴权拦截器测试：覆盖关闭、放行（未配密钥）、放行（密钥正确）、拒绝（密钥错误）四种路径。
 */
class ApiKeyAuthInterceptorTest {

    private DouyinProperties props() {
        DouyinProperties p = new DouyinProperties();
        p.setSecurityHeader("X-API-Key");
        return p;
    }

    private HttpServletRequest reqWith(String headerValue) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("X-API-Key")).thenReturn(headerValue);
        when(req.getRemoteAddr()).thenReturn("127.0.0.1");
        return req;
    }

    private HttpServletResponse resWithWriter(StringWriter sw) throws Exception {
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(res.getWriter()).thenReturn(new PrintWriter(sw));
        return res;
    }

    @Test
    void passWhenDisabled() throws Exception {
        DouyinProperties p = props();
        p.setSecurityEnabled(false);
        ApiKeyAuthInterceptor interceptor = new ApiKeyAuthInterceptor(p);

        assertTrue(interceptor.preHandle(reqWith(null), resWithWriter(new StringWriter()), null));
    }

    @Test
    void passWhenKeyNotConfigured() throws Exception {
        DouyinProperties p = props();
        p.setSecurityEnabled(true);
        p.setApiKey("");
        ApiKeyAuthInterceptor interceptor = new ApiKeyAuthInterceptor(p);

        assertTrue(interceptor.preHandle(reqWith(null), resWithWriter(new StringWriter()), null));
    }

    @Test
    void passWhenKeyMatches() throws Exception {
        DouyinProperties p = props();
        p.setSecurityEnabled(true);
        p.setApiKey("secret-123");
        ApiKeyAuthInterceptor interceptor = new ApiKeyAuthInterceptor(p);

        assertTrue(interceptor.preHandle(reqWith("secret-123"), resWithWriter(new StringWriter()), null));
    }

    @Test
    void rejectWhenKeyMismatch() throws Exception {
        DouyinProperties p = props();
        p.setSecurityEnabled(true);
        p.setApiKey("secret-123");
        ApiKeyAuthInterceptor interceptor = new ApiKeyAuthInterceptor(p);

        StringWriter sw = new StringWriter();
        HttpServletResponse res = resWithWriter(sw);
        boolean result = interceptor.preHandle(reqWith("wrong"), res, null);

        assertFalse(result);
        verify(res).setStatus(401);
        // 响应体为规范化的未授权 JSON
        String body = sw.toString();
        assertTrue(body.contains("\"code\":401"));
        assertTrue(body.contains("未授权"));
    }
}
