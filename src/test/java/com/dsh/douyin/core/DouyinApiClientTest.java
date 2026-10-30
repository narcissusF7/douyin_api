package com.dsh.douyin.core;

import com.dsh.douyin.common.error.DouyinApiException;
import com.dsh.douyin.config.DouyinProperties;
import com.dsh.douyin.module.shop.dto.ShopInfo;
import com.dsh.douyin.module.shop.dto.ShopListResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * 底层 API 客户端测试：用 MockRestServiceServer 拦截发往网关的请求，
 * 断言已按规范拼装公共参数与签名，并正确解析统一响应。
 */
class DouyinApiClientTest {

    private static final String GATEWAY = "https://openapi-fxg.jinritemai.com/";

    private DouyinProperties props;
    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private DouyinApiClient client;

    @BeforeEach
    void setUp() {
        props = new DouyinProperties();
        props.setAppKey("testkey");
        props.setAppSecret("testsecret");
        props.setGateway(GATEWAY);

        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new DouyinApiClient(props, restTemplate);
    }

    @Test
    void execute_postsSignedFormAndParsesData() {
        String body = "{\"err_no\":0,\"message\":\"success\","
                + "\"data\":{\"total\":1,\"shops\":[{\"shop_id\":1,\"shop_name\":\"测试店\"}]},"
                + "\"log_id\":\"abc\"}";

        server.expect(requestTo(GATEWAY))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().string(containsString("method=product.listV2")))
                .andExpect(content().string(containsString("app_key=testkey")))
                .andExpect(content().string(containsString("param_json=")))
                .andExpect(content().string(containsString("sign=")))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        ShopListResult result = client.execute("product.listV2", new java.util.LinkedHashMap<>(), "token-xyz", ShopListResult.class);

        server.verify();
        assertEquals(Long.valueOf(1), result.getTotal());
        assertEquals(1, result.getItems().size());
        ShopInfo shop = result.getItems().get(0);
        assertEquals(Long.valueOf(1), shop.getShopId());
        assertEquals("测试店", shop.getShopName());
    }

    @Test
    void execute_throwsOnApiError() {
        String body = "{\"err_no\":40001,\"message\":\"invalid token\",\"data\":null,\"log_id\":\"x\"}";
        server.expect(requestTo(GATEWAY))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        DouyinApiException ex = assertThrows(DouyinApiException.class,
                () -> client.execute("shop.shopList", null, "bad", ShopListResult.class));
        assertEquals(40001, ex.getErrNo());
        assertTrue(ex.getMessage().contains("invalid token"));
    }
}
