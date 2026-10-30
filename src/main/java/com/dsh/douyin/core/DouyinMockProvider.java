package com.dsh.douyin.core;

import com.dsh.douyin.common.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 本地 Mock 数据提供器：当 douyin.mock.enabled=true 时由 {@link DouyinApiClient} 调用，
 * 依据 method 返回仿真响应（snake_case，与抖店开放平台真实结构一致），便于无凭证本地联调与界面演示。
 *
 * <p>仅用于本地开发/演示，生产环境务必关闭（默认值 false）。
 */
public final class DouyinMockProvider {

    private DouyinMockProvider() {
    }

    /** 依据接口方法返回完整响应信封 {"err_no":0,"message":"...","log_id":"...","data":{...}} */
    public static String buildResponse(String method, String paramJson) {
        String data;
        switch (method) {
            case "token.create":
            case "token.refresh":
                data = tokenJson();
                break;
            case "shop.shopList":
                data = shopListJson();
                break;
            case "product.listV2":
                data = productListJson();
                break;
            case "product.getDetail":
                data = productDetailJson(paramJson);
                break;
            case "order.searchList":
                data = orderListJson();
                break;
            case "order.detail":
                data = orderDetailJson(paramJson);
                break;
            case "material.searchMaterial":
                data = materialListJson();
                break;
            case "compass.shop.dailySummary":
            case "compass.shop.tradeTrend":
            case "compass.product.list":
            case "compass.shop.sourceSummary":
                data = compassJson();
                break;
            default:
                data = "{}";
        }
        return "{\"err_no\":0,\"message\":\"success (mock)\",\"log_id\":\"mock-log-0001\",\"data\":" + data + "}";
    }

    private static String idFromParam(String paramJson, String key) {
        try {
            JsonNode node = JsonUtils.mapper().readTree(paramJson);
            JsonNode v = node.get(key);
            return v == null ? null : v.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private static String tokenJson() {
        return "{\"access_token\":\"mock_access_token_abc123\",\"expires_in\":604800,"
                + "\"refresh_token\":\"mock_refresh_token_xyz789\",\"shop_id\":1234567890,"
                + "\"shop_name\":\"抖店Mock旗舰店\","
                + "\"scope\":\"shop.product.order.material.compas\"}";
    }

    private static String shopListJson() {
        return "{\"shops\":[{\"shop_id\":1234567890,\"shop_name\":\"抖店Mock旗舰店\","
                + "\"open_id\":\"mock_open_id_001\"}],\"total\":1}";
    }

    private static String productListJson() {
        return "{\"list\":["
                + "{\"product_id\":6900001,\"name\":\"【Mock】纯棉短袖T恤 夏季新款\",\"status\":1,"
                + "\"check_status\":2,\"img\":\"https://picsum.photos/seed/p1/200\","
                + "\"create_time\":\"2026-09-01 10:00:00\",\"update_time\":\"2026-10-01 12:00:00\","
                + "\"category_id\":\"12345\"},"
                + "{\"product_id\":6900002,\"name\":\"【Mock】蓝牙耳机 主动降噪\",\"status\":1,"
                + "\"check_status\":2,\"img\":\"https://picsum.photos/seed/p2/200\","
                + "\"create_time\":\"2026-09-05 11:30:00\",\"update_time\":\"2026-10-10 09:20:00\","
                + "\"category_id\":\"12345\"}],\"total\":2}";
    }

    private static String productDetailJson(String paramJson) {
        String id = idFromParam(paramJson, "product_id");
        String pid = (id == null) ? "6900001" : id;
        return "{\"product_id\":" + pid + ",\"name\":\"【Mock】商品详情示例-" + pid + "\",\"status\":1,"
                + "\"check_status\":2,\"img\":\"https://picsum.photos/seed/detail/300\","
                + "\"create_time\":\"2026-09-01 10:00:00\",\"update_time\":\"2026-10-20 15:00:00\","
                + "\"category_id\":\"12345\"}";
    }

    private static String orderListJson() {
        return "{\"list\":["
                + "{\"order_id\":\"69202409010001\",\"parent_order_id\":\"69202409010000\","
                + "\"order_status\":1,\"pay_amount\":\"199.00\",\"post_addr\":\"北京市朝阳区xx路1号\","
                + "\"create_time\":\"2026-10-20 14:00:00\",\"update_time\":\"2026-10-20 14:05:00\",\"pay_type\":1},"
                + "{\"order_id\":\"69202409010002\",\"parent_order_id\":\"69202409010000\","
                + "\"order_status\":2,\"pay_amount\":\"399.00\",\"post_addr\":\"上海市浦东新区yy路2号\","
                + "\"create_time\":\"2026-10-21 09:00:00\",\"update_time\":\"2026-10-21 09:10:00\",\"pay_type\":2}"
                + "],\"total\":2}";
    }

    private static String orderDetailJson(String paramJson) {
        String id = idFromParam(paramJson, "order_id");
        String oid = (id == null) ? "69202409010001" : id;
        return "{\"order_id\":\"" + oid + "\",\"parent_order_id\":\"69202409010000\","
                + "\"order_status\":1,\"pay_amount\":\"199.00\",\"post_addr\":\"北京市朝阳区xx路1号\","
                + "\"create_time\":\"2026-10-20 14:00:00\",\"update_time\":\"2026-10-20 14:05:00\",\"pay_type\":1}";
    }

    private static String materialListJson() {
        return "{\"list\":["
                + "{\"material_id\":\"m1001\",\"material_name\":\"主图-夏日海报\",\"material_type\":0,"
                + "\"material_url\":\"https://picsum.photos/seed/m1/400\","
                + "\"create_time\":\"2026-09-10 10:00:00\",\"material_size\":12345},"
                + "{\"material_id\":\"m1002\",\"material_name\":\"视频-开箱测评\",\"material_type\":1,"
                + "\"material_url\":\"https://picsum.photos/seed/m2/400\","
                + "\"create_time\":\"2026-09-12 11:00:00\",\"material_size\":56789}"
                + "],\"total\":2}";
    }

    private static String compassJson() {
        return "{\"shop_id\":1234567890,\"date\":\"2026-10-29\","
                + "\"summary\":{\"pay_order_count\":328,\"pay_amount\":\"86542.30\","
                + "\"visitor_count\":12840,\"order_convert_rate\":\"0.0255\","
                + "\"refund_amount\":\"1200.00\",\"add_cart_count\":2100},"
                + "\"trend\":[{\"hour\":\"00\",\"pay_amount\":\"1200.00\"},"
                + "{\"hour\":\"12\",\"pay_amount\":\"9800.00\"}]}";
    }
}
