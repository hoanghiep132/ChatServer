package com.hiepnh.chatserver.service;

import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.model.response.BaseResponse;
import com.hiepnh.chatserver.model.response.GetSingleItemResponse;
import com.hiepnh.chatserver.repository.UserRepository;
import com.hiepnh.chatserver.utils.AppUtils;
import com.hiepnh.chatserver.websocket.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public BaseResponse signUp(UserEntity user){
        BaseResponse response = new BaseResponse();
        GetSingleItemResponse<UserEntity> resp = getByUsername(user.getUsername());
        if(resp.getItem() != null){
            response.setResult(-1, "Tên người dùng đã tồn tại");
            return response;
        }
        String pwd = AppUtils.hashString(user.getPassword());
        user.setPassword(pwd);
        user.setStatus(1);
        try {
            userRepository.save(user);
            response.setSuccess();
        }catch (Exception ex){
            response.setResult(-1, "Error");
        }
        return response;
    }

    public GetSingleItemResponse<UserEntity> login(String username, String password){
        String pwd = AppUtils.hashString(password);
        Optional<UserEntity> userEntity = userRepository.findByUsernameAndPassword(username, pwd);
        GetSingleItemResponse<UserEntity> response = new GetSingleItemResponse<>();
        if(userEntity.isPresent()){
            UserManager.getInstance().addUser(username);
            response.setSuccess();
            response.setItem(userEntity.get());
        }else {
            response.setResult(-1, "error");
        }
        return response;
    }

    public GetSingleItemResponse<UserEntity> getById(Integer id){
        Optional<UserEntity> userEntity = userRepository.findById(id);
        GetSingleItemResponse<UserEntity> response = new GetSingleItemResponse<>();
        if(userEntity.isPresent()){
            response.setSuccess();
            response.setItem(userEntity.get());
        }else {
            response.setResult(-1, "error");
        }
        return response;
    }

    public GetSingleItemResponse<UserEntity> getByUsername(String username){
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        GetSingleItemResponse<UserEntity> response = new GetSingleItemResponse<>();
        if(userEntity.isPresent()){
            response.setSuccess();
            response.setItem(userEntity.get());
        }else {
            response.setResult(-1, "error");
        }
        return response;
    }
}
