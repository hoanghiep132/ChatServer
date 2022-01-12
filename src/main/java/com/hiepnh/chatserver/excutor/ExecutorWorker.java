package com.hiepnh.chatserver.excutor;

import com.hiepnh.chatserver.common.MessageType;
import com.hiepnh.chatserver.model.MessageModel;
import com.hiepnh.chatserver.model.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class ExecutorWorker extends ThreadBase{

    private final Logger logger = LoggerFactory.getLogger(getName());

    private final BlockingQueue<MessageChannel> queue;


    public ExecutorWorker(String name, BlockingQueue<MessageChannel> queue) {
        super(name);
        this.queue = queue;
    }

    @Override
    protected void onExecuting() throws Exception {
        logger.info("Thread {} started", getName());
    }

    @Override
    protected void onKilling() {
        this.kill();
        logger.info("Thread {} killed", getName());
    }

    @Override
    protected void onException(Throwable th) {
        logger.error("onException: ", th);
    }

    @Override
    protected long sleepTime() throws Exception {
        return 100;
    }

    @Override
    protected void action() {
        MessageChannel messageChannel = queue.poll();
        if(messageChannel == null){
            return;
        }
        if(messageChannel.getMessage().getTag() == MessageType.ONLINE){
            String receiver = messageChannel.getMessage().getReceiver();
            String content;
            if(messageChannel.getMessage().getContent().contains(";" + receiver)){
                content = messageChannel.getMessage().getContent().replaceAll(";" + receiver, "");
            }else if(messageChannel.getMessage().getContent().contains(receiver + ";")){
                content = messageChannel.getMessage().getContent().replaceAll(receiver + ";", "");
            }else {
                content = messageChannel.getMessage().getContent();
            }
            MessageModel message = new MessageModel();
            message.setTag(MessageType.ONLINE);
            message.setContent(content);
            messageChannel.getChannel().writeAndFlush(message);
        }else if(messageChannel.getMessage().getTag() == MessageType.MESSAGE){
            messageChannel.getChannel().writeAndFlush(messageChannel.getMessage());
        }else if(messageChannel.getMessage().getTag() == MessageType.TYPING){
            messageChannel.getChannel().writeAndFlush(messageChannel.getMessage());
        }else if(messageChannel.getMessage().getTag() == MessageType.CALL_REQUEST){
            messageChannel.getChannel().writeAndFlush(messageChannel.getMessage());
        }else if(messageChannel.getMessage().getTag() == MessageType.CALL){
            messageChannel.getChannel().writeAndFlush(messageChannel.getMessage());
        }
    }
}
