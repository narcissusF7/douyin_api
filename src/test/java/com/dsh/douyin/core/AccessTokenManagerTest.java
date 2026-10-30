package com.dsh.douyin.core;

import com.dsh.douyin.common.error.BusinessException;
import com.dsh.douyin.core.model.TokenResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * access_token 管理器测试：校验未授权抛错、正常取用，以及临近过期自动刷新。
 */
class AccessTokenManagerTest {

    private AuthService authService = mock(AuthService.class);

    private TokenResult token(String access, String refresh, Long expiresIn) {
        TokenResult t = new TokenResult();
        t.setAccessToken(access);
        t.setRefreshToken(refresh);
        t.setExpiresIn(expiresIn);
        t.setShopId(100L);
        return t;
    }

    @Test
    void getAccessToken_throwsWhenNotAuthorized() {
        AccessTokenManager manager = new AccessTokenManager(authService);
        assertThrows(BusinessException.class, () -> manager.getAccessToken(999L));
    }

    @Test
    void getAccessToken_returnsCachedTokenWithoutRefresh() {
        AccessTokenManager manager = new AccessTokenManager(authService);
        manager.store(1L, token("access-1", "refresh-1", 7L * 24 * 3600));

        assertEquals("access-1", manager.getAccessToken(1L));
        verify(authService, times(0)).refreshToken(anyString());
    }

    @Test
    void getAccessToken_refreshesWhenExpired() {
        AccessTokenManager manager = new AccessTokenManager(authService);
        // expiresIn=1 -> 实际过期时间（now+1s-5min缓冲）已落在上一个时刻，触发刷新
        manager.store(2L, token("access-old", "refresh-old", 1L));

        when(authService.refreshToken("refresh-old"))
                .thenReturn(token("access-new", "refresh-new", 7L * 24 * 3600));

        assertEquals("access-new", manager.getAccessToken(2L));
        verify(authService, times(1)).refreshToken("refresh-old");
    }
}
