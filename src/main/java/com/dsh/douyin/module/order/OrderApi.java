package com.dsh.douyin.module.order;

import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.DouyinApiClient;
import com.dsh.douyin.module.order.dto.OrderInfo;
import com.dsh.douyin.module.order.dto.OrderListQuery;
import com.dsh.douyin.module.order.dto.OrderListResult;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单 API（order.*）。
 */
@Service
public class OrderApi {

    private final DouyinApiClient client;
    private final AccessTokenManager tokenManager;

    public OrderApi(DouyinApiClient client, AccessTokenManager tokenManager) {
        this.client = client;
        this.tokenManager = tokenManager;
    }

    /** 订单列表查询（method=order.searchList） */
    public OrderListResult listOrders(Long shopId, OrderListQuery query) {
        String token = tokenManager.getAccessToken(shopId);
        return client.execute("order.searchList", query, token, OrderListResult.class);
    }

    /** 订单详情（method=order.detail） */
    public OrderInfo getDetail(Long shopId, String orderId) {
        String token = tokenManager.getAccessToken(shopId);
        Map<String, Object> biz = new LinkedHashMap<>();
        biz.put("order_id", orderId);
        return client.execute("order.detail", biz, token, OrderInfo.class);
    }
}
