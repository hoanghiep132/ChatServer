package com.hiepnh.chatserver.netty_server;

import com.hiepnh.chatserver.excutor.OnlineUserListHandler;
import com.hiepnh.chatserver.excutor.PackageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private static boolean running = false;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int port;

    public NettyServer() {
        this.port = 29000;
    }

    public void start() {
        PackageHandler packageHandler = new PackageHandler();
        ChatServerInitializer chatServerInitializer = new ChatServerInitializer(packageHandler);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(chatServerInitializer);

            serverBootstrap.bind(port).sync();
            logger.info("Netty server start successfully");
        } catch (Exception ex) {
            logger.error("Netty server start error : ", ex);
        }

        running = true;
    }
}
