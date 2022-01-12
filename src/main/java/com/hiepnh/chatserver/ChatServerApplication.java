package com.hiepnh.chatserver;

import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.netty_server.NettyServer;
import com.hiepnh.chatserver.repository.UserRepository;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }


    @PostConstruct
    public void startNettyServer(){
        new NettyServer().start();
    }


}
