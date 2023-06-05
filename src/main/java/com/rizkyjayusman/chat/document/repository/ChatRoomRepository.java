package com.rizkyjayusman.chat.document.repository;

import com.rizkyjayusman.chat.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUid(String uid);
}
