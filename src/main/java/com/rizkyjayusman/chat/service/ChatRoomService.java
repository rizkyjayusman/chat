package com.rizkyjayusman.chat.service;

import com.rizkyjayusman.chat.document.ChatRoom;
import com.rizkyjayusman.chat.document.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> getAllChatRoom() {
        return chatRoomRepository.findAll();
    }

    public Optional<ChatRoom> getChatRoomByUid(String uid) {
        return chatRoomRepository.findByUid(uid);
    }

    public void createChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }
}
