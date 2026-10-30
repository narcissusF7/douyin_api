package com.dsh.douyin.security;

import com.dsh.douyin.common.model.Result;
import com.dsh.douyin.common.util.JsonUtils;
import com.dsh.douyin.config.DouyinProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对外接口 API-Key 鉴权拦截器。
 *
 * <p>规则：
 * <ul>
 *   <li>securityEnabled=false 时直接放行（关闭鉴权）；</li>
 *   <li>securityEnabled=true 但未配置 apiKey 时，打印 WARN 并放行（仅用于本地联调，生产务必配置）；</li>
 *   <li>配置 apiKey 后，要求请求头携带与配置一致的密钥，否则返回 401。</li>
 * </ul>
 *
 * <p>排除项：OAuth 回调（/api/douyin/auth/**）与消息推送接收（/api/douyin/push/**）不走此鉴权，
 * 前者由平台回跳、后者由消息签名校验保护，均见 WebConfig 的注册排除规则。
 */
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthInterceptor.class);

    private final DouyinProperties props;

    public ApiKeyAuthInterceptor(DouyinProperties props) {
        this.props = props;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!props.isSecurityEnabled()) {
            return true;
        }
        String expected = props.getApiKey();
        if (!StringUtils.hasText(expected)) {
            log.warn("[API-AUTH] 鉴权已开启但未配置 api-key，已降级为放行（生产环境请设置 DOUYIN_API_KEY）");
            return true;
        }
        String provided = request.getHeader(props.getSecurityHeader());
        if (expected.equals(provided)) {
            return true;
        }
        log.warn("[API-AUTH] 鉴权失败：缺少或错误的 {} 请求头，来自 {}",
                props.getSecurityHeader(), request.getRemoteAddr());
        writeUnauthorized(response);
        return false;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtils.toJson(Result.fail(401, "未授权：缺少或错误的 API-Key")));
    }
}
