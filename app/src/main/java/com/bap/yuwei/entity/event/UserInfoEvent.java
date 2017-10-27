package com.bap.yuwei.entity.event;


import com.bap.yuwei.entity.User;

/**
 * Created by jianhua on 17/5/31.
 */

public class UserInfoEvent {

    public User user;


    public UserInfoEvent() {
    }

    public UserInfoEvent(User user) {
        this.user = user;
    }
}
