package com.example.rightbackend.chat.domain.repository;

import com.example.rightbackend.chat.domain.Message;
import com.example.rightbackend.chat.domain.model.ChatMessageSummary;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomMessageRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void markMessagesAsRead(Long chatRoomId, Long readBy) {
        Query query = new Query(Criteria.where("roomId").is(chatRoomId)
                .and("receiverId").is(readBy)
                .and("isRead").is(false));
        Update update = new Update();
        update.set("isRead", true);
        mongoTemplate.updateMulti(query, update, Message.class);
    }

    @Override
    public List<Message> findMessages(Long chatRoomID, String lastMessageId) {
        return findMessages(chatRoomID, lastMessageId, 20); // 기본 20개
    }

    @Override
    public List<Message> findMessages(Long chatRoomID, String lastMessageId, int limit) {
        Query query = new Query(
                Criteria.where("roomId").is(chatRoomID)
        ).with(Sort.by(Sort.Direction.DESC, "_id")).limit(Math.min(limit, 100)); // 최대 100개 제한

        if (lastMessageId != null && !lastMessageId.trim().isEmpty()) {
            try {
                query.addCriteria(Criteria.where("_id").lt(new ObjectId(lastMessageId)));
            } catch (IllegalArgumentException e) {
                // 잘못된 ObjectId 형식인 경우 무시하고 처음부터 조회
                // 로그는 남기지만 예외를 던지지 않음
            }
        }

        List<Message> messages = mongoTemplate.find(query, Message.class);
        // 시간순으로 정렬 (오래된 메시지부터)
        messages.sort((m1, m2) -> m1.getSendTime().compareTo(m2.getSendTime()));
        return messages;
    }

    @Override
    public List<ChatMessageSummary> aggregateMessageSummaries(List<Long> roomIds, Long memberId) {
        Criteria matchCriteria = Criteria.where("roomId").in(roomIds);
        AggregationOperation match = Aggregation.match(matchCriteria);

        AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "sendTime");

        AggregationOperation group = Aggregation.group("roomId")
                .first("roomId").as("roomId")
                .first("content").as("lastMessageContent")
                .first("sendTime").as("lastMessageTime")
                .sum(ConditionalOperators
                        .when(new Criteria().andOperator(
                                Criteria.where("receiverId").is(memberId),
                                Criteria.where("isRead").is(false)
                        ))
                        .then(1)
                        .otherwise(0))
                .as("numberOfUnreadMessages");

        Aggregation aggregation = Aggregation.newAggregation(match, sort, group);

        AggregationResults<ChatMessageSummary> results = mongoTemplate.aggregate(
                aggregation, "message", ChatMessageSummary.class);
        return new ArrayList<>(results.getMappedResults());
    }
}