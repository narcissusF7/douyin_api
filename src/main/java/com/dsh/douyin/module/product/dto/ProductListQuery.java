package com.dsh.douyin.module.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * product.listV2 查询参数。
 * 时间字段格式：yyyy-MM-dd HH:mm:ss（GMT+8）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductListQuery {

    /** 页码，默认 0 */
    private Integer page = 0;

    /** 每页条数，最大 100，默认 10 */
    private Integer size = 10;

    /** 商品状态：0-在线 1-下线 2-删除 */
    private Integer status;

    /** 审核状态：1-未提交 2-待审核 3-审核通过 4-审核未通过 5-封禁 7-审核通过待上架 */
    private Integer checkStatus;

    private String createTimeStart;
    private String createTimeEnd;
    private String updateTimeStart;
    private String updateTimeEnd;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(String updateTimeStart) {
        this.updateTimeStart = updateTimeStart;
    }

    public String getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(String updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
    }
}
