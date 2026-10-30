package com.dsh.douyin.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 默认消息处理器：记录收到的推送事件（catch-all）。
 * 接入真实业务时，新增专门的 {@link MessageHandler} 实现（如订单创建、库存变更），
 * Spring 会自动注册并参与分发；本类始终处理全部标签，便于观察。
 */
@Component
public class LoggingMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingMessageHandler.class);

    @Override
    public boolean supports(String tag) {
        return true;
    }

    @Override
    public void handle(MessageEvent event) {
        log.info("[PUSH] 收到消息 tag={} msgId={} payload={}", event.getTag(), event.getMsgId(), event.getPayload());
    }
}
