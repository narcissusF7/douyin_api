package com.dsh.douyin.core;

import com.dsh.douyin.common.error.BusinessException;
import com.dsh.douyin.config.DouyinProperties;
import com.dsh.douyin.core.model.TokenResult;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 授权服务：构造授权页 URL、用授权码换取 access_token、刷新 access_token。
 * token 接口本身不需要 access_token，故调用时传 null。
 */
@Service
public class AuthService {

    private final DouyinApiClient client;
    private final DouyinProperties props;

    public AuthService(DouyinApiClient client, DouyinProperties props) {
        this.client = client;
        this.props = props;
    }

    /**
     * 构造第三方应用授权页 URL，引导商家点击授权。商家授权后携带 code 跳回 redirectUri。
     */
    public String buildAuthorizeUrl(String state, String redirectUri) {
        String redirect = (redirectUri != null && !redirectUri.isEmpty())
                ? redirectUri : props.getRedirectUri();
        if (redirect == null || redirect.isEmpty()) {
            if (props.getMock().isEnabled()) {
                redirect = "http://localhost:8080/api/douyin/auth/callback";
            } else {
                throw new BusinessException("未配置 redirect-uri，无法构造授权 URL");
            }
        }
        return UriComponentsBuilder.fromHttpUrl(props.getAuthorizeUrl())
                .queryParam("client_key", props.getAppKey())
                .queryParam("response_type", "code")
                .queryParam("scope", props.getScope())
                .queryParam("state", state == null ? "" : state)
                .queryParam("redirect_uri", redirect)
                .build()
                .toUriString();
    }

    /**
     * 用授权码换取 access_token（method=token.create）。
     */
    public TokenResult createToken(String code) {
        Map<String, Object> biz = new LinkedHashMap<>();
        biz.put("code", code);
        biz.put("grant_type", "authorization_code");
        return client.execute("token.create", biz, null, TokenResult.class);
    }

    /**
     * 用 refresh_token 刷新 access_token（method=token.refresh）。
     */
    public TokenResult refreshToken(String refreshToken) {
        Map<String, Object> biz = new LinkedHashMap<>();
        biz.put("grant_type", "refresh_token");
        biz.put("refresh_token", refreshToken);
        return client.execute("token.refresh", biz, null, TokenResult.class);
    }
}
