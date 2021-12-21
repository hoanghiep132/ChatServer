package com.hiepnh.chatserver.service;

import com.hiepnh.chatserver.entities.InteractionUserEntity;
import com.hiepnh.chatserver.model.response.BaseResponse;
import com.hiepnh.chatserver.model.response.GetArrayResponse;
import com.hiepnh.chatserver.repository.InteractionUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InteractionUserService {

    private final InteractionUserRepository interactionUserRepository;


    public InteractionUserService(InteractionUserRepository interactionUserRepository) {
        this.interactionUserRepository = interactionUserRepository;
    }

    public GetArrayResponse<InteractionUserEntity> getListUserByHistory(int id){
        GetArrayResponse<InteractionUserEntity> response = new GetArrayResponse<>();
        try {
            List<InteractionUserEntity> entities = interactionUserRepository.getListUserByHistory(id);
            response.setRows(entities);
            response.setSuccess();
            return response;
        }catch (Exception ex){
            response.setResult(-1, "error");
            return response;
        }
    }

    public BaseResponse updateInteraction(InteractionUserEntity interactionUser){
        BaseResponse response = new BaseResponse();

        return response;
    }
}
