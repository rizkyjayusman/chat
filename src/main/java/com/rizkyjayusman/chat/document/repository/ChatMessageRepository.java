package com.rizkyjayusman.chat.document.repository;

import com.rizkyjayusman.chat.document.ChatMessage;
import com.rizkyjayusman.chat.document.query.ChatMessageQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, ChatMessageNativeRepository {

}

interface ChatMessageNativeRepository {

    Page<ChatMessage> findAll(ChatMessageQuery param, Pageable pageable);
    long count(ChatMessageQuery param);
    void readAll(ChatMessageQuery param, Date readDate);

}

@RequiredArgsConstructor
class ChatMessageNativeRepositoryImpl implements ChatMessageNativeRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ChatMessage> findAll(ChatMessageQuery param, Pageable pageable) {
        Query query = param.getQuery();
        long totalCount = mongoTemplate.count(query, ChatMessageQuery.class);

        query.with(pageable);

        List<ChatMessage> results = mongoTemplate.find(query, ChatMessage.class);

        return new PageImpl<>(results,pageable, totalCount);
    }

    @Override
    public long count(ChatMessageQuery param) {
        Query query = param.getQuery();
        return mongoTemplate.count(query, ChatMessage.class);
    }

    @Override
    public void readAll(ChatMessageQuery param, Date readDate) {
        Query query = param.getQuery();

        Update update = new Update();
        update.set(ChatMessage.Fields.readByList.concat(".$.")
                .concat(ChatMessage.ReadChatStatusDto.Fields.readDate), readDate);

        mongoTemplate.updateMulti(query, update, ChatMessage.class);
    }
}
