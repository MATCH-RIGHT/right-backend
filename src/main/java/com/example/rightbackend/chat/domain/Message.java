package com.example.rightbackend.chat.domain;

import com.example.rightbackend.chat.controller.dto.request.MessageSendRequest;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "message")
@Getter @Setter
public class Message {

    @Id
    private String id;

    private String content;

    private Long roomId;

    private Long senderId;

    private Long receiverId;

    private Date sendTime;

    private Boolean isRead;

    protected Message() {}

    public static Message from(MessageSendRequest messageSendRequest) {
        Message message = new Message();
        message.content = messageSendRequest.content();
        message.roomId = messageSendRequest.roomId();
        message.senderId = messageSendRequest.senderId();
        message.receiverId = messageSendRequest.receiverId();
        message.sendTime = new Date();
        message.isRead = false;
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", roomId=" + roomId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", sendTime=" + sendTime +
                ", isRead=" + isRead +
                '}';
    }
}