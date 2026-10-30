package com.dsh.douyin.module.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 店铺基础信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopInfo {

    private Long shopId;
    private String shopName;
    private String openId;

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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
