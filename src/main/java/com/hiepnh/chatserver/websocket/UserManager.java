package com.hiepnh.chatserver.websocket;

import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private static UserManager instance;

    private Set<String> users;

    private UserManager() {
        users=  new HashSet<>();
    }

    public static synchronized UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(String username){
        if(!users.contains(username)){
            users.add(username);
        }
    }

    public Set<String> getUsers(){
        return users;
    }
}
