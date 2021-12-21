package com.hiepnh.chatserver.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserModel {

    private String username;

    private Channel channel;

    private long time;
}
