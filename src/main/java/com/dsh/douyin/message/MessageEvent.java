package com.dsh.douyin.message;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 一条接收到的消息推送事件。
 */
public class MessageEvent {

    /** 消息标签（事件类型），如 order.TradeCreate / product.StockUpdate */
    private String tag;

    /** 平台消息 ID，用于去重 */
    private String msgId;

    /** 消息载荷（原始 JSON 结构） */
    private JsonNode payload;

    /** 本服务接收到的时间戳（epoch 毫秒） */
    private long receivedAt;

    public MessageEvent() {
    }

    public MessageEvent(String tag, String msgId, JsonNode payload, long receivedAt) {
        this.tag = tag;
        this.msgId = msgId;
        this.payload = payload;
        this.receivedAt = receivedAt;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }
}
