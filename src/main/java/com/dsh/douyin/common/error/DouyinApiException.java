package com.dsh.douyin.common.error;

/**
 * 抖店接口调用异常：平台返回 err_no != 0 时抛出。
 */
public class DouyinApiException extends RuntimeException {

    private final int errNo;
    private final String logId;

    public DouyinApiException(int errNo, String message, String logId) {
        super("Douyin API error err_no=" + errNo + ", message=" + message + ", log_id=" + logId);
        this.errNo = errNo;
        this.logId = logId;
    }

    public int getErrNo() {
        return errNo;
    }

    public String getLogId() {
        return logId;
    }
}
