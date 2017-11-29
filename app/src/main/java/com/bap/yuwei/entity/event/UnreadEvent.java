package com.bap.yuwei.entity.event;

/**
 * 未读消息数量event
 */
public class UnreadEvent {

    public  int unreadNum;

    public UnreadEvent() {
    }

    public UnreadEvent(int unreadNum) {
        this.unreadNum = unreadNum;
    }
}
