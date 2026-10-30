package com.dsh.douyin.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 消息推送签名校验测试：token/timestamp/nonce 字典序拼接后 SHA1 应与平台一致。
 * 黄金向量（外部计算）：sha1("1234567890" + "nonce123" + "push_token") =
 * EC9C28A362970C545E3314A0DE415656F4EFCF81
 */
class PushSignatureVerifierTest {

    private final PushSignatureVerifier verifier = new PushSignatureVerifier();

    @Test
    void verify_acceptsValidSignature() {
        String signature = "EC9C28A362970C545E3314A0DE415656F4EFCF81";
        assertTrue(verifier.verify("push_token", signature, "1234567890", "nonce123"));
    }

    @Test
    void verify_rejectsTamperedSignature() {
        assertFalse(verifier.verify("push_token", "deadbeef", "1234567890", "nonce123"));
    }

    @Test
    void verify_rejectsMissingParams() {
        assertFalse(verifier.verify("push_token", "EC9C28A362970C545E3314A0DE415656F4EFCF81", null, "nonce123"));
    }
}
