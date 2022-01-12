package com.hiepnh.chatserver.excutor;

import com.hiepnh.chatserver.common.Constant;
import com.hiepnh.chatserver.common.MessageType;
import com.hiepnh.chatserver.connection.GetConnection;
import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.model.*;
import com.hiepnh.chatserver.utils.AppUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class OnlineUserListHandler {

    private static OnlineUserListHandler instance;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConcurrentMap<String, ChannelModel> onlineUserMap;

    private List<UserVideoPair> userVideoPairList;

    private final BlockingQueue<MessageChannel> messageQueue;

    private final GetConnection connection = new GetConnection();

    private final int MAX_TIME_OUT = 30000;
    private final int SCHEDULER_TIME = 5;
    private final int INIT_DELAY = 0;
    private final int MANAGER_THREAD_NUMBER = 1;
    private final int WORKER_THREAD_NUMBER = 10;

    public static OnlineUserListHandler getInstance(){
        if(instance == null){
            instance = new OnlineUserListHandler();
        }
        return instance;
    }

    private OnlineUserListHandler() {
        this.onlineUserMap = new ConcurrentHashMap<>();
        this.userVideoPairList = new ArrayList<>();
        messageQueue = new LinkedBlockingQueue<>();
        initManagerWorker();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(MANAGER_THREAD_NUMBER);
        Runnable baseThread = new BaseThread();
        executor.scheduleWithFixedDelay(baseThread, INIT_DELAY, SCHEDULER_TIME, TimeUnit.SECONDS);
    }

    private void initManagerWorker() {
        int numWorker = WORKER_THREAD_NUMBER;
        String name;
        for (int i = 0; i < numWorker; i++) {
            name = String.format("ExecuteWorker %d", (i + 1));
            ExecutorWorker worker = new ExecutorWorker(name, messageQueue);
            worker.execute();
        }
    }

    public void addConnection(String username, Channel channel){
        synchronized (onlineUserMap){
            long currTime = System.currentTimeMillis();
            if(onlineUserMap.containsKey(username)){
                ChannelModel channelModel = onlineUserMap.get(username);
                if(channel == channelModel.getChannel()){
                    logger.info("Client {} Ping {} ", username, currTime);
                    channelModel.setTime(currTime);
                    onlineUserMap.put(username, channelModel);
                }else {
                    channelModel.setChannel(channel);
                    channelModel.setTime(currTime);
                    MessageChannel messageChannel = new MessageChannel();
                    MessageModel message = new MessageModel();
                    String content = String.join(";", this.onlineUserMap.keySet());
                    message.setContent(content);
                    message.setTag(MessageType.ONLINE);
                    message.setReceiver(username);
                    messageChannel.setMessage(message);
                    messageChannel.setChannel(channel);
                    logger.info("Client {} reconnect to server", username);
                    onlineUserMap.put(username, channelModel);
                    messageQueue.offer(messageChannel);
                }
            }else {
                ChannelModel channelModel = new ChannelModel();
                channelModel.setChannel(channel);
                channelModel.setTime(currTime);

                MessageChannel messageChannel = new MessageChannel();
                MessageModel message = new MessageModel();
                String content = this.onlineUserMap.entrySet().stream().map(Map.Entry::getKey)
                        .collect(Collectors.joining(";"));
                message.setContent(content);
                message.setTag(MessageType.ONLINE);
                message.setReceiver(username);

                messageChannel.setMessage(message);
                messageChannel.setChannel(channel);
                this.onlineUserMap.put(username, channelModel);
            }
        }
    }

    public void removeConnection(String username){
        synchronized (onlineUserMap){
            onlineUserMap.remove(username);
        }
    }

    public void sendMessage(String sender, String receiver, String content){
        UserEntity userEntity = connection.findUserByUsername(receiver);
        if(userEntity == null || userEntity.getUsername() == null){
            return;
        }
        MessageModel message = new MessageModel();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTag(MessageType.MESSAGE);
        logger.info("Sender : {}, Receiver: {}, Content: {}", sender, receiver, content);
        ChannelModel channelModel;
        synchronized (onlineUserMap){
            channelModel = onlineUserMap.get(receiver);
            if(channelModel == null){
                Runnable dbTask = () -> connection.saveMessage(message);
                dbTask.run();
                return;
            }
        }
        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setMessage(message);
        messageChannel.setChannel(channelModel.getChannel());

        Runnable dbTask = () -> connection.saveMessage(message);
        dbTask.run();

        messageQueue.offer(messageChannel);
    }

    public void typing(String sender, String receiver){
        ChannelModel channelModel;
        synchronized (onlineUserMap){
            channelModel = onlineUserMap.get(receiver);
            if(channelModel == null){
                return;
            }
        }
        MessageModel message = new MessageModel();
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setTag(MessageType.TYPING);
        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setMessage(message);
        messageChannel.setChannel(channelModel.getChannel());
        messageQueue.offer(messageChannel);
    }

    public void transferVideo(TlvPackage tlvPackage, Channel channel){
        MessageModel message = new MessageModel();
        message.setTag(MessageType.CALL);
        byte[] data= tlvPackage.getValues();
        message.setVideoData(data);

        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setMessage(message);
        Optional<UserVideoPair> pairOptional = userVideoPairList.stream()
                .filter(e -> e.getChannel2() == channel || e.getChannel1() == channel)
                .findFirst();
        if(!pairOptional.isPresent()){
            return;
        }
        Channel partnerChannel = pairOptional.get().getPartnerChannel(channel);
        if (partnerChannel == null){
            return;
        }
        messageChannel.setChannel(partnerChannel);
        messageQueue.offer(messageChannel);
    }

    public void createUserPair(Channel sender, String receiver) {
        logger.info("Call request : {}", receiver);
        UserVideoPair userVideoPair = new UserVideoPair();
        userVideoPair.setChannel1(sender);
        boolean check = false;
        String senderUsername = null;
        for (Iterator<Map.Entry<String, ChannelModel>> iter = onlineUserMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, ChannelModel> entry = iter.next();
            if(entry.getKey().equals(receiver)){
                userVideoPair.setChannel2(entry.getValue().getChannel());
                check = true;
            }
            if(entry.getValue().getChannel() == sender){
                senderUsername = entry.getKey();
            }
        }
        MessageModel message = new MessageModel();
        MessageChannel messageChannel = new MessageChannel();

        if(check && senderUsername != null){
           userVideoPair.setStatus(Constant.UserPairStatus.REQUEST);
           userVideoPairList.add(userVideoPair);
           message.setTag(MessageType.CALL_REQUEST);
           message.setSender(senderUsername);
           messageChannel.setChannel(userVideoPair.getChannel2());
        }else {
            message.setTag(MessageType.USER_UNAVAILABLE);
            messageChannel.setChannel(sender);
        }
        messageChannel.setMessage(message);
        messageQueue.offer(messageChannel);
    }

    public void acceptCallRequest(Channel channel){
        logger.info("accept");
        Optional<UserVideoPair> pairOptional = userVideoPairList.stream()
                .filter(e -> e.getChannel2() == channel).findFirst();
        if(!pairOptional.isPresent()){
            return;
        }
        pairOptional.get().setStatus(Constant.UserPairStatus.RUNNING);
        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setChannel(pairOptional.get().getChannel1());
        MessageModel messageModel = new MessageModel();
        messageModel.setTag(MessageType.CALL_ACCEPT);
        messageChannel.setMessage(messageModel);
        messageQueue.offer(messageChannel);
    }

    public void rejectCallRequest(Channel channel){
        logger.info("reject");
        Iterator<UserVideoPair> iterator = userVideoPairList.iterator();
        while (iterator.hasNext()){
            UserVideoPair pair = iterator.next();
            if(pair.getChannel2() == channel){
                iterator.remove();
                MessageChannel messageChannel = new MessageChannel();
                messageChannel.setChannel(pair.getChannel1());
                MessageModel messageModel = new MessageModel();
                messageModel.setTag(MessageType.CALL_REJECT);
                messageQueue.offer(messageChannel);
            }
        }
        Optional<UserVideoPair> pairOptional = userVideoPairList.stream()
                .filter(e -> e.getChannel2() == channel).findFirst();
        if(!pairOptional.isPresent()){
            return;
        }

    }

    private class BaseThread implements Runnable{
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for(Iterator<Map.Entry<String, ChannelModel>> iter = onlineUserMap.entrySet().iterator(); iter.hasNext(); ){
                Map.Entry<String, ChannelModel> entry = iter.next();
                if(currentTime - entry.getValue().getTime() > MAX_TIME_OUT){
                    logger.info("User {} disconnect ", entry.getKey());
                    iter.remove();
                }
            }
            String content = onlineUserMap.entrySet().stream().map(Map.Entry::getKey)
                    .collect(Collectors.joining(";"));

            for(Iterator<Map.Entry<String, ChannelModel>> iter = onlineUserMap.entrySet().iterator(); iter.hasNext(); ){
                Map.Entry<String, ChannelModel> entry = iter.next();
                MessageModel message = new MessageModel();
                message.setTag(MessageType.ONLINE);
                message.setReceiver(entry.getKey());
                message.setContent(content);

                MessageChannel messageChannel = new MessageChannel();
                messageChannel.setMessage(message);
                messageChannel.setChannel(entry.getValue().getChannel());

                messageQueue.offer(messageChannel);
            }
        }
    }
}
