package com.hiepnh.chatserver.repository;

import com.hiepnh.chatserver.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    @Query(nativeQuery = true,value = "select * from message msg where " +
            " (msg.sender_id = ?1 or msg.sender_id = ?2) " +
            " and " +
            " (msg.receiver_id = ?1 or msg.receiver_id = ?2) " +
            " order by msg.time desc limit 1")
    Optional<MessageEntity> findLastMessageByPairUser(Integer user1Id, Integer user2Id);

    @Query(nativeQuery = true,value = "select * from message msg where " +
            " (msg.sender_id = ?1 or msg.sender_id = ?2) " +
            " and " +
            " (msg.receiver_id = ?1 or msg.receiver_id = ?2) " +
            " order by msg.time DESC limit ?4 offset ?3")
    List<MessageEntity> getMessageByPairUser(Integer user1Id, Integer user2Id, int offset, int limit);


}
