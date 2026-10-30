package com.dsh.douyin.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * 抖店开放平台签名工具（MD5）。
 *
 * <p>规则（官方调用指南）：
 * <ol>
 *   <li>除 sign 外，所有请求参数（含公共参数与 param_json）按参数名 ASCII 升序排列；</li>
 *   <li>依次拼接为 key + value（无需 URL encode）；</li>
 *   <li>首尾包裹 app_secret：app_secret + 拼接串 + app_secret；</li>
 *   <li>对结果做 MD5，再转大写。</li>
 * </ol>
 */
public final class SignUtil {

    private SignUtil() {
    }

    public static String sign(Map<String, String> params, String appSecret) {
        TreeMap<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if ("sign".equals(entry.getKey()) || entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            sb.append(entry.getKey()).append(entry.getValue());
        }
        String raw = appSecret + sb + appSecret;
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8)).toUpperCase();
    }
}
