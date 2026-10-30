package com.dsh.douyin.common.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 签名工具测试：以预计算（外部计算）的 MD5 黄金向量做断言，确保签名算法与官方一致。
 * app_secret = test_secret
 */
class SignUtilTest {

    private static final String SECRET = "test_secret";

    @Test
    void sign_ordersParamsAndWrapsSecret_md5Upper() {
        // a,b 排序拼接 -> "a1b2"，首尾包裹 secret -> "test_secreta1b2test_secret"
        Map<String, String> params = new LinkedHashMap<>();
        params.put("a", "1");
        params.put("b", "2");
        assertEquals("BDEB7868EB0AA2A693140C528E4BC7DC", SignUtil.sign(params, SECRET));
    }

    @Test
    void sign_isOrderIndependent() {
        // 乱序传入应得到与正序一致的结果（按 key ASCII 排序）
        Map<String, String> p1 = new LinkedHashMap<>();
        p1.put("a", "1");
        p1.put("b", "2");
        Map<String, String> p2 = new LinkedHashMap<>();
        p2.put("b", "2");
        p2.put("a", "1");
        assertEquals(SignUtil.sign(p1, SECRET), SignUtil.sign(p2, SECRET));
    }

    @Test
    void sign_skipsEmptyAndSignKey() {
        // 空值参数与 sign 本身不参与签名
        Map<String, String> withEmpty = new LinkedHashMap<>();
        withEmpty.put("a", "1");
        withEmpty.put("b", "");
        Map<String, String> withSign = new LinkedHashMap<>();
        withSign.put("a", "1");
        withSign.put("sign", "xyz");
        String expected = "4D9473296ADCE1626EFEF9E2ECD51D5E";
        assertEquals(expected, SignUtil.sign(withEmpty, SECRET));
        assertEquals(expected, SignUtil.sign(withSign, SECRET));
    }

    @Test
    void sign_differenceParamsProduceDifferentSign() {
        Map<String, String> p1 = new LinkedHashMap<>();
        p1.put("a", "1");
        Map<String, String> p2 = new LinkedHashMap<>();
        p2.put("a", "2");
        assertNotEquals(SignUtil.sign(p1, SECRET), SignUtil.sign(p2, SECRET));
    }
}
