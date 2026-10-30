package com.dsh.douyin.module.shop.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * shop.shopList 返回结果。不同文档对容器字段命名略有差异，用 @JsonAlias 兼容。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopListResult {

    @JsonAlias({"shops", "list", "data"})
    private List<ShopInfo> items;

    @JsonAlias({"total", "count"})
    private Long total;

    public List<ShopInfo> getItems() {
        return items;
    }

    public void setItems(List<ShopInfo> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
