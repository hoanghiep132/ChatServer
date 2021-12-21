package com.hiepnh.chatserver.netty_server;

import com.hiepnh.chatserver.excutor.PackageHandler;
import com.hiepnh.chatserver.netty_server.inbound.RequestDecoderHandler;
import com.hiepnh.chatserver.netty_server.outbound.ResponseEncoderHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private PackageHandler packageHandler;

    public ChatServerInitializer(PackageHandler packageHandler) {
        super();
        this.packageHandler = packageHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new RequestDecoderHandler(packageHandler));
        pipeline.addLast(new ResponseEncoderHandler());
    }
}
