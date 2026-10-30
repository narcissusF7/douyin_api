package com.dsh.douyin.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 抖店开放平台统一响应信封：{"err_no":0,"message":"success","data":{...},"log_id":"..."}。
 * data 为业务载荷，结构随接口变化，故此处保留 JsonNode 由调用方按需转换。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResult {

    /** 错误码，0 表示成功 */
    private int errNo;

    /** 错误描述 */
    private String message;

    /** 链路日志 ID，排查问题时提供给平台 */
    private String logId;

    /** 业务数据 */
    private JsonNode data;

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
