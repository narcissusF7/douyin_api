package com.dsh.douyin.module.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 订单详情。字段较多，按需扩充；未知字段忽略。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderInfo {

    private String orderId;
    private String parentOrderId;
    private Integer orderStatus;
    private String payAmount;
    private String postAddr;
    private String createTime;
    private String updateTime;
    private Integer payType;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPostAddr() {
        return postAddr;
    }

    public void setPostAddr(String postAddr) {
        this.postAddr = postAddr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}
