package com.hiepnh.chatserver.excutor;

import com.hiepnh.chatserver.common.MessageType;
import com.hiepnh.chatserver.model.TlvPackage;
import com.hiepnh.chatserver.utils.AppUtils;
import io.netty.channel.Channel;

public class PackageHandler {

    private OnlineUserListHandler onlineUserListHandler;

    public PackageHandler() {
        this.onlineUserListHandler = OnlineUserListHandler.getInstance();
    }

    public void addPackage(TlvPackage tlvPackage, Channel channel) {
        if(tlvPackage.getTag() == MessageType.CONNECT){
            String usernameConnect = AppUtils.convertByteArrayToString(tlvPackage.getValues());
            clientConnect(usernameConnect, channel);
        }else if(tlvPackage.getTag() == MessageType.DISCONNECT){
            String usernameDisconnect = AppUtils.convertByteArrayToString(tlvPackage.getValues());
            clientDisconnect(usernameDisconnect);
        }else if(tlvPackage.getTag() == MessageType.MESSAGE){
            transferMessage(AppUtils.convertByteArrayToString(tlvPackage.getValues()));
        }else if(tlvPackage.getTag() == MessageType.TYPING){
            String content = AppUtils.convertByteArrayToString(tlvPackage.getValues());
            typing(content);
        }else if(tlvPackage.getTag() == MessageType.CALL_REQUEST){
            String receiver = AppUtils.convertByteArrayToString(tlvPackage.getValues());
            callRequest(channel, receiver);
        }else if(tlvPackage.getTag() == MessageType.CALL_ACCEPT){
            acceptRequest(channel);
        }else if(tlvPackage.getTag() == MessageType.CALL_REJECT){
            rejectRequest(channel);
        }else if(tlvPackage.getTag() == MessageType.CALL){
            transferVideo(tlvPackage, channel);
        }

    }

    private void clientConnect(String username, Channel channel) {
        onlineUserListHandler.addConnection(username, channel);
    }

    private void clientDisconnect(String username) {
        onlineUserListHandler.removeConnection(username);
    }

    private void transferMessage(String value){
        String[] data = value.split(";",3);
        if(data.length != 3){
            return;
        }
        String sender = data[0];
        String receiver = data[1];
        String content = data[2];

        onlineUserListHandler.sendMessage(sender, receiver, content);
    }

    private void transferVideo(TlvPackage tlvPackage, Channel channel){
        onlineUserListHandler.transferVideo(tlvPackage, channel);
    }

    private void typing(String value){
        String[] list = value.split(";");
        if(list.length != 2 ){
            return;
        }
        onlineUserListHandler.typing(list[0], list[1]);
    }

    private void callRequest(Channel channel, String receiver){
        onlineUserListHandler.createUserPair(channel, receiver);
    }

    private void acceptRequest(Channel channel){
        onlineUserListHandler.acceptCallRequest(channel);
    }

    private void rejectRequest(Channel channel){
        onlineUserListHandler.rejectCallRequest(channel);
    }
}
