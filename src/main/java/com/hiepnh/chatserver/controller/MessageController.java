package com.hiepnh.chatserver.controller;

import com.hiepnh.chatserver.entities.MessageEntity;
import com.hiepnh.chatserver.model.MessageModel;
import com.hiepnh.chatserver.model.response.BaseResponse;
import com.hiepnh.chatserver.model.response.GetArrayResponse;
import com.hiepnh.chatserver.model.response.GetSingleItemResponse;
import com.hiepnh.chatserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public BaseResponse sendMsg(@RequestBody MessageModel msg){
        BaseResponse response = new BaseResponse();

        return response;
    }

    @GetMapping("/last")
    public GetSingleItemResponse<MessageEntity> getLastMessage(
            @RequestParam("id1") Integer id1,
            @RequestParam("id2") Integer id2){
        return messageService.getLastMessageByPairUser(id1, id2);
    }

    @GetMapping("/list")
    public GetArrayResponse<MessageEntity> getLastMessage(
            @RequestParam("id1") Integer id1,
            @RequestParam("id2") Integer id2,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit){
        return messageService.getMessageByPairUser(id1, id2, offset, limit);
    }
}
