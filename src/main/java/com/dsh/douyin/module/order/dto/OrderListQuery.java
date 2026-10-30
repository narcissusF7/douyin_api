package com.dsh.douyin.module.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * order.searchList 查询参数。
 * 时间字段格式：yyyy-MM-dd HH:mm:ss（GMT+8）。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderListQuery {

    /** 订单状态，详见抖店订单状态机 */
    private Integer orderState;

    private String startTime;
    private String endTime;

    /** 页码，默认 0 */
    private Integer page = 0;

    /** 每页条数，默认 10 */
    private Integer size = 10;

    /** 排序字段，如 "create_time" */
    private String orderBy;

    /** 是否降序 */
    private Boolean isDesc;

    /** 指定商品 ID 过滤 */
    private Long productId;

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
