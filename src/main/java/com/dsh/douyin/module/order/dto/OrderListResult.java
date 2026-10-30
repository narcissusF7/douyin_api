package com.dsh.douyin.module.order.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * order.searchList 返回结果。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderListResult {

    @JsonAlias({"data", "list", "order_list"})
    private List<OrderInfo> items;

    @JsonAlias({"total", "count", "total_num"})
    private Long total;

    public List<OrderInfo> getItems() {
        return items;
    }

    public void setItems(List<OrderInfo> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
