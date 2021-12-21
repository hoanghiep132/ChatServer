package com.hiepnh.chatserver.service;

import com.hiepnh.chatserver.entities.MessageEntity;
import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.entities.UserEntityWithoutAvatar;
import com.hiepnh.chatserver.model.MessageModel;
import com.hiepnh.chatserver.model.response.BaseResponse;
import com.hiepnh.chatserver.model.response.GetArrayResponse;
import com.hiepnh.chatserver.model.response.GetSingleItemResponse;
import com.hiepnh.chatserver.repository.MessageRepository;
import com.hiepnh.chatserver.repository.UserRepository;
import com.hiepnh.chatserver.repository.UserWithoutAvatarRepository;
import com.hiepnh.chatserver.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWithoutAvatarRepository userWithoutAvatarRepository;

    @Autowired
    private MessageRepository messageRepository;

    public BaseResponse saveMessage(MessageModel message){
        BaseResponse response = new BaseResponse();
        MessageEntity messageEntity = new MessageEntity();

        Optional<UserEntityWithoutAvatar> sender = userWithoutAvatarRepository.findByUsername(message.getSender());
        Optional<UserEntityWithoutAvatar> receiver = userWithoutAvatarRepository.findByUsername(message.getReceiver());
        messageEntity.setContent(message.getContent());
        messageEntity.setReceiver(receiver.get());
        messageEntity.setSender(sender.get());
        messageEntity.setStatus(1);
        messageEntity.setType(1);
        messageEntity.setTime(System.currentTimeMillis());
        messageRepository.save(messageEntity);

        response.setSuccess();
        return  response;
    }

    public GetSingleItemResponse<MessageEntity> getLastMessageByPairUser(Integer user1Id, Integer user2Id){
        GetSingleItemResponse<MessageEntity> response = new GetSingleItemResponse<>();

        Optional<MessageEntity> messageEntity = messageRepository.findLastMessageByPairUser(user1Id, user2Id);
        if(!messageEntity.isPresent()){
            response.setResult(-1, "Error");
            return response;
        }
        response.setSuccess();
        response.setItem(messageEntity.get());

        return response;
    }

    public GetArrayResponse<MessageEntity> getMessageByPairUser(Integer user1Id, Integer user2Id, int offset, int limit){
        GetArrayResponse<MessageEntity> response = new GetArrayResponse<>();
        if(offset < 0){
            offset = 0;
        }
        if(limit < 0){
            limit = 0;
        }
        int val1 = offset * limit;
        int val2 = val1 + limit - 1;

        List<MessageEntity> messageEntities = messageRepository.getMessageByPairUser(user1Id, user2Id, val1, val2);
        response.setRows(messageEntities);
        response.setSuccess();
        return response;
    }
}
