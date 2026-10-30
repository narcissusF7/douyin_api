package com.dsh.douyin.module.product;

import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.DouyinApiClient;
import com.dsh.douyin.module.product.dto.ProductInfo;
import com.dsh.douyin.module.product.dto.ProductListQuery;
import com.dsh.douyin.module.product.dto.ProductListResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 商品 API（product.*）。
 */
@Service
public class ProductApi {

    private final DouyinApiClient client;
    private final AccessTokenManager tokenManager;

    public ProductApi(DouyinApiClient client, AccessTokenManager tokenManager) {
        this.client = client;
        this.tokenManager = tokenManager;
    }

    /** 批量查询商品列表（method=product.listV2） */
    public ProductListResult listProducts(Long shopId, ProductListQuery query) {
        String token = tokenManager.getAccessToken(shopId);
        return client.execute("product.listV2", query, token, ProductListResult.class);
    }

    /** 获取商品详情（method=product.getDetail） */
    public ProductInfo getDetail(Long shopId, Long productId) {
        String token = tokenManager.getAccessToken(shopId);
        Map<String, Object> biz = new LinkedHashMap<>();
        biz.put("product_id", productId);
        return client.execute("product.getDetail", biz, token, ProductInfo.class);
    }
}
