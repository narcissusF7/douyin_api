package com.dsh.douyin.controller;

import com.dsh.douyin.common.error.BusinessException;
import com.dsh.douyin.common.model.Result;
import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.AuthService;
import com.dsh.douyin.core.model.TokenResult;
import com.dsh.douyin.module.data.DataApi;
import com.dsh.douyin.module.data.dto.CompassQuery;
import com.dsh.douyin.module.material.MaterialApi;
import com.dsh.douyin.module.material.dto.MaterialListResult;
import com.dsh.douyin.module.material.dto.MaterialQuery;
import com.dsh.douyin.module.order.OrderApi;
import com.dsh.douyin.module.order.dto.OrderInfo;
import com.dsh.douyin.module.order.dto.OrderListQuery;
import com.dsh.douyin.module.order.dto.OrderListResult;
import com.dsh.douyin.module.product.ProductApi;
import com.dsh.douyin.module.product.dto.ProductInfo;
import com.dsh.douyin.module.product.dto.ProductListQuery;
import com.dsh.douyin.module.product.dto.ProductListResult;
import com.dsh.douyin.module.shop.ShopApi;
import com.dsh.douyin.module.shop.dto.ShopListResult;
import com.dsh.douyin.message.MessageDispatchService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 抖店开放平台集成演示控制器：覆盖授权、店铺、商品、订单、素材、经营数据。
 * 仅作联调示例，生产环境应叠加鉴权、限流与参数校验。
 */
@RestController
@RequestMapping("/api/douyin")
@Tag(name = "抖店业务接口", description = "授权、店铺、商品、订单、素材、经营数据；除授权回调外均需 X-API-Key 鉴权")
public class DouyinDemoController {

    private final AuthService authService;
    private final AccessTokenManager tokenManager;
    private final ShopApi shopApi;
    private final ProductApi productApi;
    private final OrderApi orderApi;
    private final MaterialApi materialApi;
    private final DataApi dataApi;
    private final MessageDispatchService messageDispatchService;

    public DouyinDemoController(AuthService authService, AccessTokenManager tokenManager,
                                ShopApi shopApi, ProductApi productApi, OrderApi orderApi,
                                MaterialApi materialApi, DataApi dataApi,
                                MessageDispatchService messageDispatchService) {
        this.authService = authService;
        this.tokenManager = tokenManager;
        this.shopApi = shopApi;
        this.productApi = productApi;
        this.orderApi = orderApi;
        this.materialApi = materialApi;
        this.dataApi = dataApi;
        this.messageDispatchService = messageDispatchService;
    }

    /** 构造授权页 URL，引导商家点击授权 */
    @GetMapping("/auth/url")
    @Operation(summary = "构造授权页 URL", description = "生成商家授权页地址，引导商家点击授权；无需 X-API-Key")
    public Result<String> authorizeUrl(@Parameter(description = "自定义状态值，原样回传") @RequestParam(required = false) String state,
                                       @Parameter(description = "覆盖默认回调地址（需与平台后台一致）") @RequestParam(required = false) String redirectUri) {
        return Result.ok(authService.buildAuthorizeUrl(state, redirectUri));
    }

    /** 授权回调：商家授权后携带 code 跳回此处，用 code 换取并缓存 token */
    @GetMapping("/auth/callback")
    @Operation(summary = "授权回调换 Token", description = "平台授权后携带 code 跳回，用 code 换取 access_token/refresh_token 并缓存；无需 X-API-Key")
    public Result<?> callback(@Parameter(description = "授权码 code（必填）", required = true) @RequestParam String code) {
        TokenResult token = authService.createToken(code);
        if (token.getShopId() == null) {
            throw new BusinessException("授权失败：未返回 shop_id");
        }
        tokenManager.store(token.getShopId(), token);
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("shopId", token.getShopId());
        info.put("shopName", token.getShopName());
        info.put("expiresIn", token.getExpiresIn());
        return Result.ok(info);
    }

    @GetMapping("/shops")
    @Operation(summary = "店铺列表", description = "获取当前授权账号下的店铺列表")
    public Result<ShopListResult> shops(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId) {
        return Result.ok(shopApi.listShops(shopId));
    }

    @GetMapping("/products")
    @Operation(summary = "商品列表", description = "分页查询商品（支持状态/审核状态筛选）")
    public Result<ProductListResult> products(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                              @Parameter(description = "页码，从 0 开始") @RequestParam(required = false, defaultValue = "0") int page,
                                              @Parameter(description = "每页条数") @RequestParam(required = false, defaultValue = "10") int size,
                                              @Parameter(description = "商品状态筛选（可选）") @RequestParam(required = false) Integer status,
                                              @Parameter(description = "审核状态筛选（可选）") @RequestParam(required = false) Integer checkStatus) {
        ProductListQuery query = new ProductListQuery();
        query.setPage(page);
        query.setSize(size);
        query.setStatus(status);
        query.setCheckStatus(checkStatus);
        return Result.ok(productApi.listProducts(shopId, query));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "商品详情", description = "按商品 ID 获取商品详情")
    public Result<ProductInfo> productDetail(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                             @Parameter(description = "商品 ID（路径参数，必填）", required = true) @PathVariable Long productId) {
        return Result.ok(productApi.getDetail(shopId, productId));
    }

    @GetMapping("/orders")
    @Operation(summary = "订单列表", description = "分页查询订单（支持订单状态与下单时间区间筛选）")
    public Result<OrderListResult> orders(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                          @Parameter(description = "订单状态（可选）") @RequestParam(required = false) Integer orderState,
                                          @Parameter(description = "下单开始时间 yyyy-MM-dd HH:mm:ss（可选）") @RequestParam(required = false) String startTime,
                                          @Parameter(description = "下单结束时间 yyyy-MM-dd HH:mm:ss（可选）") @RequestParam(required = false) String endTime,
                                          @Parameter(description = "页码，从 0 开始") @RequestParam(required = false, defaultValue = "0") int page,
                                          @Parameter(description = "每页条数") @RequestParam(required = false, defaultValue = "10") int size) {
        OrderListQuery query = new OrderListQuery();
        query.setOrderState(orderState);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setPage(page);
        query.setSize(size);
        return Result.ok(orderApi.listOrders(shopId, query));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "订单详情", description = "按订单 ID 获取订单详情")
    public Result<OrderInfo> orderDetail(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                         @Parameter(description = "订单 ID（路径参数，必填）", required = true) @PathVariable String orderId) {
        return Result.ok(orderApi.getDetail(shopId, orderId));
    }

    @GetMapping("/materials")
    @Operation(summary = "素材列表", description = "分页查询素材库（支持素材类型筛选）")
    public Result<MaterialListResult> materials(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                                @Parameter(description = "素材类型（可选）") @RequestParam(required = false) Integer materialType,
                                                @Parameter(description = "页码，从 0 开始") @RequestParam(required = false, defaultValue = "0") int pageNo,
                                                @Parameter(description = "每页条数") @RequestParam(required = false, defaultValue = "10") int pageSize) {
        MaterialQuery query = new MaterialQuery();
        query.setMaterialType(materialType);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        return Result.ok(materialApi.search(shopId, query));
    }

    @GetMapping("/data/daily-summary")
    @Operation(summary = "经营数据 · 日汇总", description = "抖店罗盘经营数据日汇总，返回原始 JSON 以适配多变字段")
    public Result<JsonNode> dailySummary(@Parameter(description = "店铺 ID（必填）", required = true) @RequestParam Long shopId,
                                         @Parameter(description = "统计开始时间（可选）") @RequestParam(required = false) String startTime,
                                         @Parameter(description = "统计结束时间（可选）") @RequestParam(required = false) String endTime) {
        CompassQuery query = new CompassQuery();
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        return Result.ok(dataApi.dailySummary(shopId, query));
    }

    /** 查询最近收到的消息推送事件（联调查看用，受 API-Key 鉴权保护） */
    @GetMapping("/messages")
    @Operation(summary = "推送记录查询", description = "查询最近收到的消息推送事件（联调查看用）")
    public Result<?> recentMessages() {
        return Result.ok(messageDispatchService.recent());
    }
}
