package com.dsh.douyin.module.data;

import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.DouyinApiClient;
import com.dsh.douyin.module.data.dto.CompassQuery;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * 经营数据 API（抖店罗盘 compass.*）。
 *
 * <p>注意：抖店罗盘接口的方法名与参数随平台版本演进，下方为常用命名，落地前请以
 * <a href="https://op.jinritemai.com">抖店开放平台</a> 罗盘（compass）文档为准，
 * 必要时调整 method 名称与 CompassQuery 字段。返回 JsonNode 以适配多变的响应结构。
 */
@Service
public class DataApi {

    private final DouyinApiClient client;
    private final AccessTokenManager tokenManager;

    public DataApi(DouyinApiClient client, AccessTokenManager tokenManager) {
        this.client = client;
        this.tokenManager = tokenManager;
    }

    /** 店铺核心指标汇总（compass.shop.dailySummary） */
    public JsonNode dailySummary(Long shopId, CompassQuery query) {
        return call("compass.shop.dailySummary", shopId, query);
    }

    /** 交易趋势（compass.shop.tradeTrend） */
    public JsonNode tradeTrend(Long shopId, CompassQuery query) {
        return call("compass.shop.tradeTrend", shopId, query);
    }

    /** 商品排行榜（compass.product.list） */
    public JsonNode productList(Long shopId, CompassQuery query) {
        return call("compass.product.list", shopId, query);
    }

    /** 流量来源分析（compass.shop.sourceSummary） */
    public JsonNode sourceSummary(Long shopId, CompassQuery query) {
        return call("compass.shop.sourceSummary", shopId, query);
    }

    private JsonNode call(String method, Long shopId, CompassQuery query) {
        String token = tokenManager.getAccessToken(shopId);
        return client.execute(method, query, token, JsonNode.class);
    }
}
