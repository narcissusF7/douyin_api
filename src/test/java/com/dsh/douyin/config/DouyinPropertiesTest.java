package com.dsh.douyin.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 配置校验测试：凭证缺失应快速失败；配置齐全应通过。
 */
class DouyinPropertiesTest {

    @Test
    void validate_throwsWhenCredentialsMissing() {
        DouyinProperties props = new DouyinProperties();
        props.setAppKey("");
        props.setAppSecret("");
        assertThrows(IllegalStateException.class, props::validate);
    }

    @Test
    void validate_passesWhenConfigured() {
        DouyinProperties props = new DouyinProperties();
        props.setAppKey("my-key");
        props.setAppSecret("my-secret");
        assertDoesNotThrow(props::validate);
    }
}
