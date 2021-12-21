package com.hiepnh.chatserver.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ChannelModel {

    private Channel channel;

    private long time;
}
