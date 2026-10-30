package com.dsh.douyin.core.model;

/**
 * 进程内令牌缓存条目，带过期时间（提前 5 分钟视为过期，触发刷新）。
 */
public class TokenEntry {

    private String accessToken;
    private String refreshToken;
    /** 过期时间戳（epoch 毫秒） */
    private long expireAt;

    private static final long REFRESH_BUFFER_MS = 5 * 60 * 1000L;
    private static final long DEFAULT_TTL_SECONDS = 7L * 24 * 3600;

    public static TokenEntry from(TokenResult token) {
        TokenEntry entry = new TokenEntry();
        entry.accessToken = token.getAccessToken();
        entry.refreshToken = token.getRefreshToken();
        long ttl = (token.getExpiresIn() != null && token.getExpiresIn() > 0)
                ? token.getExpiresIn() : DEFAULT_TTL_SECONDS;
        entry.expireAt = System.currentTimeMillis() + ttl * 1000L - REFRESH_BUFFER_MS;
        return entry;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expireAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
