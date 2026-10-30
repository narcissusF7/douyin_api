package com.dsh.douyin.message;

import com.dsh.douyin.common.error.BusinessException;
import com.dsh.douyin.common.model.Result;
import com.dsh.douyin.common.util.JsonUtils;
import com.dsh.douyin.config.DouyinProperties;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息推送接收端点（/api/douyin/push）。
 *
 * <p>对应开放平台「消息订阅 / 消息推送」能力：
 * <ul>
 *   <li>GET：平台在后台配置推送 URL 时发起的校验请求，校验通过后原样回显 echostr；</li>
 *   <li>POST：平台推送的业务事件（订单、库存、退款等），先校验签名再分发处理；</li>
 * </ul>
 * 该端点已排除 API-Key 鉴权（WebConfig），改由消息签名校验保护，仅响应来自抖音的请求。
 */
@RestController
@RequestMapping("/api/douyin/push")
@Tag(name = "消息推送订阅", description = "接收抖店开放平台消息推送：GET 校验 URL，POST 接收业务事件；由消息签名（SHA1）保护")
public class MessagePushController {

    private final DouyinProperties props;
    private final PushSignatureVerifier verifier;
    private final MessageDispatchService dispatchService;

    public MessagePushController(DouyinProperties props, PushSignatureVerifier verifier,
                                 MessageDispatchService dispatchService) {
        this.props = props;
        this.verifier = verifier;
        this.dispatchService = dispatchService;
    }

    /** 配置推送 URL 时的签名校验，通过则原样返回 echostr */
    @GetMapping
    @Operation(summary = "推送 URL 校验", description = "平台配置消息推送地址时发起的 GET 校验，签名正确则原样回显 echostr；由消息签名保护，无需 X-API-Key")
    public String verify(@Parameter(description = "签名（SHA1(排序(token,timestamp,nonce)拼接)）", required = true) @RequestParam String signature,
                        @Parameter(description = "时间戳", required = true) @RequestParam String timestamp,
                        @Parameter(description = "随机串", required = true) @RequestParam String nonce,
                        @Parameter(description = "校验回显串", required = true) @RequestParam String echostr) {
        if (!props.isMessageEnabled()) {
            return "";
        }
        if (verifier.verify(props.getMessageToken(), signature, timestamp, nonce)) {
            return echostr;
        }
        return "";
    }

    /** 接收业务事件推送 */
    @PostMapping
    @Operation(summary = "接收业务事件推送", description = "平台推送的业务事件（订单/库存/退款等），先校验签名再分发到对应处理器；由消息签名保护，无需 X-API-Key")
    public Result<String> receive(@Parameter(description = "签名", required = true) @RequestParam String signature,
                                  @Parameter(description = "时间戳", required = true) @RequestParam String timestamp,
                                  @Parameter(description = "随机串", required = true) @RequestParam String nonce,
                                  @Parameter(description = "事件 JSON 体，含 tag/msg_id/msg 字段", required = true) @RequestBody String body) {
        if (!props.isMessageEnabled()) {
            throw new BusinessException("消息推送未启用");
        }
        if (!verifier.verify(props.getMessageToken(), signature, timestamp, nonce)) {
            throw new BusinessException("消息推送签名校验失败");
        }

        JsonNode root = JsonUtils.fromJson(body, JsonNode.class);
        String tag = root.path("tag").asText(null);
        if (tag == null) {
            throw new BusinessException("推送消息缺少 tag 字段");
        }
        String msgId = root.path("msg_id").asText(null);
        if (msgId == null) {
            msgId = root.path("msgId").asText(null);
        }
        JsonNode payload = root.has("msg") ? root.get("msg") : root;

        MessageEvent event = new MessageEvent(tag, msgId, payload, System.currentTimeMillis());
        dispatchService.dispatch(event);
        return Result.ok("success");
    }
}
