package com.hiepnh.chatserver.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageModel implements Serializable {

    private byte tag;

    private String sender;

    private String receiver;

    private byte[] videoData;

    private String content;

    private long time;
}
