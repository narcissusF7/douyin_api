package com.dsh.douyin.module.material.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 素材信息。未知字段忽略。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialInfo {

    private String materialId;
    private String materialName;
    private Integer materialType;
    private String materialUrl;
    private String createTime;
    private Long materialSize;

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Integer materialType) {
        this.materialType = materialType;
    }

    public String getMaterialUrl() {
        return materialUrl;
    }

    public void setMaterialUrl(String materialUrl) {
        this.materialUrl = materialUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(Long materialSize) {
        this.materialSize = materialSize;
    }
}
