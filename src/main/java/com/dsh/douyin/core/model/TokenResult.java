package com.dsh.douyin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * token.create / token.refresh 返回的令牌信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResult {

    /** 访问令牌 */
    private String accessToken;

    /** 有效期（秒），默认约 7 天 */
    private Long expiresIn;

    /** 刷新令牌，有效期约 14 天 */
    private String refreshToken;

    /** 已废弃字段，可忽略 */
    private String scope;

    /** 店铺 ID */
    private Long shopId;

    /** 店铺名称 */
    private String shopName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
