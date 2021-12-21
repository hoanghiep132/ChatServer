package com.hiepnh.chatserver.repository;

import com.hiepnh.chatserver.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Override
    Optional<UserEntity> findById(Integer integer);

    @Query(value = "from UserEntity u where u.username = ?1 ")
    Optional<UserEntity> findByUsername(String username);

    @Query(value = "from UserEntity u where u.username = ?1 and u.password = ?2")
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
