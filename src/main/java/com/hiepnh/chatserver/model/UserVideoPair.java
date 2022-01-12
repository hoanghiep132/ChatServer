package com.hiepnh.chatserver.model;

import com.hiepnh.chatserver.common.Constant;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class UserVideoPair {

    private Channel channel1;

    private Channel channel2;

    private int status;

    public Channel getPartnerChannel(Channel ch){
        if(status != Constant.UserPairStatus.RUNNING){
            return null;
        }
        if(channel1 == ch){
            return channel2;
        }else {
            return channel1;
        }
    }
}
