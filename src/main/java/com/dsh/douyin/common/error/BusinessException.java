package com.dsh.douyin.common.error;

/**
 * 业务异常：由本应用主动抛出（如未授权、参数非法等）。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
