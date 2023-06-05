package com.rizkyjayusman.chat.service;

import com.rizkyjayusman.chat.controller.param.GetChatParam;
import com.rizkyjayusman.chat.document.ChatMessage;
import com.rizkyjayusman.chat.document.query.ChatMessageQuery;
import com.rizkyjayusman.chat.document.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public Page<ChatMessage> getAllChat(String chatRoomUid, GetChatParam param) {
        ChatMessageQuery query = new ChatMessageQuery()
                .chatRoomUid(chatRoomUid);
        return chatMessageRepository.findAll(query, param.getPageable());
    }

    public long count(String chatRoomUid, String recipient, boolean isUnread) {
        ChatMessageQuery query = new ChatMessageQuery()
                .chatRoomUid(chatRoomUid)
                .recipient(recipient)
                .unread(isUnread);
        return chatMessageRepository.count(query);
    }

    public void read(String chatRoomUid, String recipient) {
        ChatMessageQuery query = new ChatMessageQuery()
                .chatRoomUid(chatRoomUid)
                .recipient(recipient)
                .unread(true);

        chatMessageRepository.readAll(query, new Date());
    }

    public void save(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

}
