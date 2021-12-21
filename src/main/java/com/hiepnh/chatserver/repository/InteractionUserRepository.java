package com.hiepnh.chatserver.repository;

import com.hiepnh.chatserver.entities.InteractionUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InteractionUserRepository extends JpaRepository<InteractionUserEntity, Integer> {

    @Query(nativeQuery = true, value = "select * from interaction_user iu " +
            " where iu.user_id = ?1 order by iu.time desc ")
    List<InteractionUserEntity> getListUserByHistory(int id);

}
