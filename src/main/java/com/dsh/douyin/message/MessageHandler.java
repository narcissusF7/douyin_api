package com.dsh.douyin.message;

/**
 * 消息处理器：按消息标签分发处理（策略模式）。
 *
 * <p>Spring 会自动收集所有 {@code MessageHandler} 实现注入到 {@link MessageDispatchService}。
 * 支持通配：supports 既可精确匹配也可按业务前缀匹配（如 "order.*"）。
 */
public interface MessageHandler {

    /** 是否处理该标签的消息 */
    boolean supports(String tag);

    /** 处理消息 */
    void handle(MessageEvent event);
}
