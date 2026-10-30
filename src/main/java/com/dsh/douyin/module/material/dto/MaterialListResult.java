package com.dsh.douyin.module.material.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * material.searchMaterial 返回结果。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialListResult {

    @JsonAlias({"data", "list", "material_list"})
    private List<MaterialInfo> items;

    @JsonAlias({"total", "count", "total_num"})
    private Long total;

    public List<MaterialInfo> getItems() {
        return items;
    }

    public void setItems(List<MaterialInfo> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
