package com.dsh.douyin.module.shop;

import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.DouyinApiClient;
import com.dsh.douyin.module.shop.dto.ShopInfo;
import com.dsh.douyin.module.shop.dto.ShopListResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 店铺 API（shop.*）。
 */
@Service
public class ShopApi {

    private final DouyinApiClient client;
    private final AccessTokenManager tokenManager;

    public ShopApi(DouyinApiClient client, AccessTokenManager tokenManager) {
        this.client = client;
        this.tokenManager = tokenManager;
    }

    /** 获取已授权店铺列表（method=shop.shopList） */
    public ShopListResult listShops(Long shopId) {
        String token = tokenManager.getAccessToken(shopId);
        return client.execute("shop.shopList", new LinkedHashMap<>(), token, ShopListResult.class);
    }
}
