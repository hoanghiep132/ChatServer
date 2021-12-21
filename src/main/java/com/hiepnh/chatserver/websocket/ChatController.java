package com.hiepnh.chatserver.websocket;

import com.hiepnh.chatserver.model.MessageModel;
import com.hiepnh.chatserver.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageService messageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat/{receiver}")
//    @SendTo("/topic/public")
    public void sendMessage(
            @DestinationVariable String receiver,
            @Payload MessageModel message) {
        logger.info("receiver : {}", receiver);
        boolean isExist = UserManager.getInstance().getUsers().contains(receiver);
        if(isExist){
            messagingTemplate.convertAndSend("/topic/messages/" + receiver, message);
            messageService.saveMessage(message);
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public MessageModel addUser(@Payload MessageModel chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
