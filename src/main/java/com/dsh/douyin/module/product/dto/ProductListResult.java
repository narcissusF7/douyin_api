package com.dsh.douyin.module.product.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * product.listV2 返回结果。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResult {

    @JsonAlias({"data", "list", "product_list"})
    private List<ProductInfo> items;

    @JsonAlias({"total", "count", "total_num"})
    private Long total;

    public List<ProductInfo> getItems() {
        return items;
    }

    public void setItems(List<ProductInfo> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
