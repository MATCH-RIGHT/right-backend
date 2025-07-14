package com.example.rightbackend.chat.domain.repository;

import com.example.rightbackend.chat.domain.Message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {
}