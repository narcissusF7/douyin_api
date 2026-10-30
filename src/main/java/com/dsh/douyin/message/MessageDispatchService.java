package com.dsh.douyin.message;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 消息分发中心：接收原始推送事件，按标签路由到对应 {@link MessageHandler}，
 * 并保留最近 N 条事件用于本地联调查看（生产建议改为持久化/消息队列）。
 */
@Service
public class MessageDispatchService {

    private final List<MessageHandler> handlers;
    private final Queue<MessageEvent> recent = new ConcurrentLinkedQueue<>();
    private static final int MAX_RECENT = 200;

    public MessageDispatchService(List<MessageHandler> handlers) {
        this.handlers = new ArrayList<>(handlers);
    }

    /** 分发一条消息 */
    public void dispatch(MessageEvent event) {
        recent.add(event);
        while (recent.size() > MAX_RECENT) {
            recent.poll();
        }
        for (MessageHandler handler : handlers) {
            if (handler.supports(event.getTag())) {
                handler.handle(event);
            }
        }
    }

    /** 查询最近收到的消息（联调用） */
    public List<MessageEvent> recent() {
        return new ArrayList<>(recent);
    }
}
