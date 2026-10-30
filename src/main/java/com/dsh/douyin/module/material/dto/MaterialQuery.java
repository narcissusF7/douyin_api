package com.dsh.douyin.module.material.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * material.searchMaterial 查询参数。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaterialQuery {

    /** 素材类型：0-图片 1-视频 */
    private Integer materialType;

    /** 页码，默认 0（部分接口从 1 开始，详见官方文档） */
    private Integer pageNo = 0;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    /** 排序字段 */
    private String orderBy;

    /** 开始时间 yyyy-MM-dd HH:mm:ss */
    private String startTime;

    /** 结束时间 yyyy-MM-dd HH:mm:ss */
    private String endTime;

    /** 素材状态 */
    private Integer materialStatus;

    public Integer getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Integer materialType) {
        this.materialType = materialType;
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
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

    public Integer getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(Integer materialStatus) {
        this.materialStatus = materialStatus;
    }
}
