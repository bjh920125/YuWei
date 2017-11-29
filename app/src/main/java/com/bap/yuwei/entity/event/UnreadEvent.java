package com.bap.yuwei.entity.event;

/**
 * 未读消息数量event
 * QueryUnreadCountEvent 事件获取数量后通过UnreadEvent将数字传给各个页面
 */
public class UnreadEvent {

    public  int unreadNum;

    public UnreadEvent() {
    }

    public UnreadEvent(int unreadNum) {
        this.unreadNum = unreadNum;
    }
}
