package com.rizkyjayusman.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rizkyjayusman.chat.document.ChatMessage;
import com.rizkyjayusman.chat.document.ChatRoom;
import com.rizkyjayusman.chat.dto.MessageDto;
import com.rizkyjayusman.chat.entity.User;
import com.rizkyjayusman.chat.service.ChatRoomService;
import com.rizkyjayusman.chat.service.ChatService;
import com.rizkyjayusman.chat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRoomService chatRoomService;

    Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        MultiValueMap<String, String> parameters = null;
        if (session.getUri() != null) {
            parameters = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();
        }

        if (parameters == null) {
            session.close();
            return;
        }

        List<String> userUid = parameters.get("userUid");
        if (userUid == null || userUid.isEmpty()) {
            session.close();
            return;
        }

        List<String> roomUid = parameters.get("chatRoomUid");
        if (roomUid == null || roomUid.isEmpty()) {
            session.close();
            return;
        }

        Optional<User> user = userService.getUserByUid(userUid.get(0));
        if (!user.isPresent()) {
            session.close();
            return;
        }

        Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomByUid(roomUid.get(0));
        if (!chatRoom.isPresent()) {
            session.close();
            return;
        }

        session.getAttributes().put("userUid", user.get().getUid());
        session.getAttributes().put("chatRoomUid", chatRoom.get().getUid());

        for (Map.Entry<String, WebSocketSession> participant : sessions.entrySet()) {
            participant.getValue().sendMessage(new TextMessage("New user was joined! : ".concat(user.get().getUsername())));
        }

        chatService.read(chatRoom.get().getUid(), user.get().getUid());

        sessions.put(user.get().getUid(), session);

        log.info("[Request] ChatWebSocketHandler.afterConnectionEstablished() :: welcome to the chat!");
        session.sendMessage(new TextMessage("Welcome to the chat!"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        MessageDto messageDto = new ObjectMapper().readValue(payload, MessageDto.class);
        if (messageDto == null || messageDto.getMessage() == null || messageDto.getMessage().equals("")) {
            log.error("[Request] ChatWebSocketHandler.handleMessage() :: bad request");
            return;
        }

        String chatRoomUid = session.getAttributes().get("chatRoomUid").toString();
        Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomByUid(chatRoomUid);
        if (! chatRoom.isPresent()) {
            log.error("[Request] ChatWebSocketHandler.handleMessage() :: there is no chat room {}", chatRoomUid);
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUid(UUID.randomUUID().toString());
        chatMessage.setRoomUid(session.getAttributes().get("chatRoomUid").toString());
        chatMessage.setSender(session.getAttributes().get("userUid").toString());
        chatMessage.setContent(messageDto.getMessage());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setRecipients(new LinkedList<>());
        chatMessage.setReadByList(new LinkedList<>());


        for (String participant : chatRoom.get().getMembers()) {
            if (session.getAttributes().containsKey("userUid") &&
                    ! participant.equals(session.getAttributes().get("userUid"))) {

                chatMessage.getRecipients().add(participant);
                ChatMessage.ReadChatStatusDto readStatusDto = new ChatMessage.ReadChatStatusDto();
                readStatusDto.setRecipient(participant);
                readStatusDto.setReadDate(null);

                if (sessions.containsKey(participant)) {
                    sessions.get(participant).sendMessage(new TextMessage(chatMessage.getContent()));
                    readStatusDto.setReadDate(new Date());
                }

                chatMessage.getReadByList().add(readStatusDto);

                log.info("[Request] ChatWebSocketHandler.handleMessage() :: sending a message {} to user {}",
                        chatMessage.getContent(), participant);
            }
        }

        chatService.save(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (session.getAttributes().containsKey("userUid")) {
            log.info("[Request] ChatWebSocketHandler.afterConnectionClosed() :: {} was logout!",
                    session.getAttributes().get("userUid").toString());

            sessions.remove(session.getAttributes().get("userUid").toString());
        }

        for (Map.Entry<String, WebSocketSession> participant : sessions.entrySet()) {
            participant.getValue().sendMessage(new TextMessage("User was logout!"));
        }
    }
}
