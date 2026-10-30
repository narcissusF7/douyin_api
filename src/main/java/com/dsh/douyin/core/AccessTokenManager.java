package com.dsh.douyin.core;

import com.dsh.douyin.common.error.BusinessException;
import com.dsh.douyin.core.model.TokenEntry;
import com.dsh.douyin.core.model.TokenResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * access_token 管理器：按 shopId 缓存令牌，临近过期时自动用 refresh_token 刷新。
 * 注意：令牌仅存于应用内存，生产环境建议落地到 Redis 等共享存储，避免多实例不一致。
 */
@Component
public class AccessTokenManager {

    private final AuthService authService;
    private final Map<Long, TokenEntry> cache = new ConcurrentHashMap<>();

    public AccessTokenManager(AuthService authService) {
        this.authService = authService;
    }

    /** 授权回调后存入令牌 */
    public void store(Long shopId, TokenResult token) {
        cache.put(shopId, TokenEntry.from(token));
    }

    /** 获取某店铺可用的 access_token，必要时自动刷新 */
    public String getAccessToken(Long shopId) {
        TokenEntry entry = cache.get(shopId);
        if (entry == null) {
            throw new BusinessException("店铺[" + shopId + "]尚未授权，请先引导商家授权");
        }
        if (entry.isExpired()) {
            synchronized (String.valueOf(shopId).intern()) {
                TokenEntry current = cache.get(shopId);
                if (current != null && current.isExpired()) {
                    TokenResult refreshed = authService.refreshToken(current.getRefreshToken());
                    entry = TokenEntry.from(refreshed);
                    cache.put(shopId, entry);
                } else {
                    entry = current;
                }
            }
        }
        return entry.getAccessToken();
    }

    public void remove(Long shopId) {
        cache.remove(shopId);
    }
}
