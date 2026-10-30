package com.dsh.douyin.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON 工具。抖店请求/响应字段均为 snake_case，故全局采用 SNAKE_CASE 命名策略，
 * 业务实体用 camelCase 字段即可自动映射。
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 解析失败: " + e.getMessage(), e);
        }
    }

    /** 将 JsonNode / Map / POJO 转换为目标类型 */
    public static <T> T convert(Object node, Class<T> type) {
        return MAPPER.convertValue(node, type);
    }

    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }
}
