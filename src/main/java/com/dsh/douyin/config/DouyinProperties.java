package com.dsh.douyin.config;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 抖音开放平台相关配置，全部来自环境变量 / application.yml，启动时集中校验、快速失败。
 * 真实密钥只出现在环境变量或 .env，绝不硬编码进代码库。
 */
@Component
@ConfigurationProperties(prefix = "douyin")
public class DouyinProperties {

    /** 应用 app_key */
    private String appKey = "";

    /** 应用 app_secret（仅服务端持有，用于签名，禁止下发前端） */
    private String appSecret = "";

    /** API 网关地址 */
    private String gateway = "https://openapi-fxg.jinritemai.com/";

    /** 授权页地址 */
    private String authorizeUrl = "https://openweb.jinritemai.com/doudian/shop/authorize";

    /** 授权回调地址（与开放平台后台配置一致） */
    private String redirectUri = "";

    /** 授权 scope */
    private String scope = "";

    /** 连接超时（毫秒） */
    private int connectTimeout = 5000;

    /** 读取超时（毫秒） */
    private int readTimeout = 10000;

    // ===== 对外接口鉴权 =====
    /** 是否开启对外接口 API-Key 鉴权（默认开启；未配置 api-key 时降级放行，详见 ApiKeyAuthInterceptor） */
    private boolean securityEnabled = true;

    /** 对外接口的 API-Key（与请求头 X-API-Key 比对） */
    private String apiKey = "";

    /** 鉴权请求头名称 */
    private String securityHeader = "X-API-Key";

    // ===== 消息推送订阅 =====
    /** 是否启用消息推送接收端点 */
    private boolean messageEnabled = true;

    /** 消息推送校验 token（平台后台配置的消息推送 token） */
    private String messageToken = "";

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(appSecret)) {
            throw new IllegalStateException(
                    "抖音开放平台凭证未配置：请设置环境变量 DOUYIN_APP_KEY / DOUYIN_APP_SECRET，" +
                            "或在 application.yml 的 douyin.app-key / douyin.app-secret 中填写（请勿提交真实密钥到代码库）。");
        }
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecurityHeader() {
        return securityHeader;
    }

    public void setSecurityHeader(String securityHeader) {
        this.securityHeader = securityHeader;
    }

    public boolean isMessageEnabled() {
        return messageEnabled;
    }

    public void setMessageEnabled(boolean messageEnabled) {
        this.messageEnabled = messageEnabled;
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }
}
