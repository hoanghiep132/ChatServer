package com.hiepnh.chatserver.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class MessageChannel {

    private MessageModel message;

    private Channel channel;
}
