package com.dsh.douyin.core;

import com.dsh.douyin.common.error.DouyinApiException;
import com.dsh.douyin.common.model.ApiResult;
import com.dsh.douyin.common.util.DateTimeUtils;
import com.dsh.douyin.common.util.JsonUtils;
import com.dsh.douyin.common.util.SignUtil;
import com.dsh.douyin.config.DouyinProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 抖店开放平台底层 HTTP 调用客户端：负责拼装公共参数、生成签名、发送请求、解析统一响应。
 * 所有业务 API 与 token 接口均复用此客户端（同一套签名规则）。
 *
 * <p>请求以 application/x-www-form-urlencoded 提交；参数值由 RestTemplate 的
 * FormHttpMessageConverter 统一做 URL 编码，避免重复编码。
 */
@Component
public class DouyinApiClient {

    private final DouyinProperties props;
    private final RestTemplate restTemplate;

    public DouyinApiClient(DouyinProperties props, RestTemplate douyinRestTemplate) {
        this.props = props;
        this.restTemplate = douyinRestTemplate;
    }

    /**
     * 执行一次抖店开放 API 调用。
     *
     * @param method       接口方法名，如 product.listV2
     * @param bizParams    业务参数对象（序列化为 param_json），可为 null
     * @param accessToken  访问令牌；token 类接口传 null
     * @param dataClass    期望的 data 反序列化类型，传 JsonNode.class 可获取原始结构
     * @param <T>          返回类型
     * @return data 反序列化结果
     */
    public <T> T execute(String method, Object bizParams, String accessToken, Class<T> dataClass) {
        String paramJson = (bizParams == null) ? "{}" : JsonUtils.toJson(bizParams);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("method", method);
        params.put("app_key", props.getAppKey());
        params.put("timestamp", DateTimeUtils.nowGmt8());
        params.put("v", "2");
        params.put("sign_method", "md5");
        params.put("format", "json");
        if (accessToken != null && !accessToken.isEmpty()) {
            params.put("access_token", accessToken);
        }
        params.put("param_json", paramJson);
        params.put("sign", SignUtil.sign(params, props.getAppSecret()));

        String respBody = doPost(params);
        ApiResult result = JsonUtils.fromJson(respBody, ApiResult.class);
        if (result.getErrNo() != 0) {
            throw new DouyinApiException(result.getErrNo(), result.getMessage(), result.getLogId());
        }
        if (dataClass == null || dataClass == Void.class) {
            return null;
        }
        return JsonUtils.convert(result.getData(), dataClass);
    }

    private String doPost(Map<String, String> params) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            form.add(entry.getKey(), entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(props.getGateway(), entity, String.class);
        return response.getBody();
    }
}
