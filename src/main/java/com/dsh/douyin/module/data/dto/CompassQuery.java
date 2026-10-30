package com.dsh.douyin.module.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 抖店罗盘（compass）通用查询参数。
 * 时间字段格式：yyyy-MM-dd HH:mm:ss（GMT+8）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompassQuery {

    private String startTime;
    private String endTime;

    /** 排序字段 */
    private String orderBy;

    /** 是否降序 */
    private Boolean isDesc;

    /** 页码，默认 0 */
    private Integer pageNo = 0;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    /** 指定商品 ID（商品榜等接口使用） */
    private Long productId;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getIsDesc() {
        return isDesc;
    }

    public void setIsDesc(Boolean isDesc) {
        this.isDesc = isDesc;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
