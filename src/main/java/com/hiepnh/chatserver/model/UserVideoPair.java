package com.hiepnh.chatserver.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserVideoPair {

    private Channel channel1;

    private Channel channel2;

    private int status;
}
