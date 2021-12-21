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

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void startNettyServer(){
        new NettyServer().start();
    }

    private void initUser(){
        UserEntity user = new UserEntity();
        user.setId(3);
        user.setFullName("tes1");
        user.setUsername("tes1");
        user.setPassword("tes1");
        user.setStatus(1);

        File file = new File("/home/hiepnguyen/Pictures/Wallpapers/test.jpg");

        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            user.setAvatar(ArrayUtils.toObject(b));
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        userRepository.save(user);
    }
}
