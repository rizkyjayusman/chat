package com.rizkyjayusman.chat.controller;

import com.rizkyjayusman.chat.controller.param.GetChatParam;
import com.rizkyjayusman.chat.document.ChatMessage;
import com.rizkyjayusman.chat.document.ChatRoom;
import com.rizkyjayusman.chat.service.ChatRoomService;
import com.rizkyjayusman.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllChatRoom();
    }

    @GetMapping("/{uid}")
    public ChatRoom getChatRoomByUid(@PathVariable("uid") String uid) {
        return chatRoomService.getChatRoomByUid(uid).orElse(null);
    }

    @PostMapping
    public void createChatRoom(@RequestBody ChatRoom chatRoom) {
        chatRoom.setUid(UUID.randomUUID().toString());
        chatRoomService.createChatRoom(chatRoom);
    }

    @GetMapping("/{chatRoomUid}/chats")
    public Page<ChatMessage> getAllChats(@PathVariable("chatRoomUid") String chatRoomUid, GetChatParam param) {
        param.setSorts(new String[]{"timestamp"});
        return chatService.getAllChat(chatRoomUid, param);
    }

    @GetMapping("/{chatRoomUid}/chats/unread")
    public long count(@PathVariable("chatRoomUid") String chatRoomUid, String recipient) {
        return chatService.count(chatRoomUid, recipient, true);
    }

}