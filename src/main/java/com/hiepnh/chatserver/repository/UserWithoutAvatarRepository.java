package com.hiepnh.chatserver.repository;

import com.hiepnh.chatserver.entities.UserEntity;
import com.hiepnh.chatserver.entities.UserEntityWithoutAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserWithoutAvatarRepository extends JpaRepository<UserEntityWithoutAvatar, Integer> {

    @Query(value = "from UserEntityWithoutAvatar u where u.username = ?1 ")
    Optional<UserEntityWithoutAvatar> findByUsername(String username);
}
