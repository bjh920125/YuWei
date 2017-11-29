package com.bap.yuwei.entity.event;


import com.bap.yuwei.entity.sys.User;

/**
 * 用户信息event
 */
public class UserInfoEvent {

    public User user;


    public UserInfoEvent() {
    }

    public UserInfoEvent(User user) {
        this.user = user;
    }
}
