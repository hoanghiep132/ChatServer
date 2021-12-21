package com.hiepnh.chatserver.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private Integer type;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private Integer status;

    @Column(name = "time")
    private Long time;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntityWithoutAvatar sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserEntityWithoutAvatar receiver;
}
