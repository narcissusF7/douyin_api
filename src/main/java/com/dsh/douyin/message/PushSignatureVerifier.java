package com.dsh.douyin.message;

import com.dsh.douyin.common.util.DateTimeUtils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 消息推送签名校验器。
 *
 * <p>抖店消息推送（消息订阅）采用与微信公众号一致的签名算法：
 * 将 <b>token、timestamp、nonce</b> 三个参数按字典序排序后用空串拼接，做 SHA1，得到 signature。
 * 平台在「配置消息推送 URL」时发起 GET 校验、在事件推送时发起 POST，均携带 signature/timestamp/nonce。
 *
 * <p>注意：确切算法以开放平台「消息推送」官方文档为准；若平台采用不同密钥/拼接方式，
 * 仅需调整 {@link #sha1OfSorted(String...)} 的实现即可。
 */
@Component
public class PushSignatureVerifier {

    /** 校验推送签名 */
    public boolean verify(String token, String signature, String timestamp, String nonce) {
        if (token == null || signature == null || timestamp == null || nonce == null) {
            return false;
        }
        String computed = sha1OfSorted(token, timestamp, nonce);
        return computed.equalsIgnoreCase(signature);
    }

    private String sha1OfSorted(String... parts) {
        List<String> list = new ArrayList<>(Arrays.asList(parts));
        Collections.sort(list);
        String joined = String.join("", list);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(joined.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-1 计算失败", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    /** 仅供测试：当前 GMT+8 时间戳（避免测试直接依赖工具类的私有 formatter） */
    static String nowStamp() {
        return DateTimeUtils.nowGmt8();
    }
}
