package com.hiepnh.chatserver.controller;

import com.hiepnh.chatserver.entities.InteractionUserEntity;
import com.hiepnh.chatserver.entities.MessageEntity;
import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.model.request.LoginRequest;
import com.hiepnh.chatserver.model.response.BaseResponse;
import com.hiepnh.chatserver.model.response.GetArrayResponse;
import com.hiepnh.chatserver.model.response.GetSingleItemResponse;
import com.hiepnh.chatserver.service.InteractionUserService;
import com.hiepnh.chatserver.service.UserService;
import com.hiepnh.chatserver.utils.AppUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/user")
@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private InteractionUserService interactionUserService;


    @GetMapping("/interaction/{id}")
    public GetArrayResponse<InteractionUserEntity> login(@PathVariable("id") Integer id){
        return interactionUserService.getListUserByHistory(id);
    }

    @PostMapping("/sign-up")
    public BaseResponse signup(
            @RequestParam(value = "info") String userJson,
            @RequestParam(value = "file") MultipartFile file){
        BaseResponse response = new BaseResponse();
        try {
            UserEntity user = AppUtils.convertJsonToObject(userJson, UserEntity.class);
            user.setAvatar(ArrayUtils.toObject(file.getBytes()));
            response = userService.signUp(user);
        } catch (IOException e) {
            response.setResult(-1, "Error");
        }
        return response;
    }


    @PostMapping("/login")
    public GetSingleItemResponse<UserEntity> login(@RequestBody LoginRequest request){
        GetSingleItemResponse<UserEntity> response = userService.login(request.getUsername(), request.getPassword());
//        logger.info("Response : {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public GetSingleItemResponse<UserEntity> findById(
            @PathVariable("id") Integer id){
        return userService.getById(id);
    }

    @GetMapping("/find")
    public GetSingleItemResponse<UserEntity> findByUsername(
            @RequestParam("username") String username){
        return userService.getByUsername(username);
    }
}
