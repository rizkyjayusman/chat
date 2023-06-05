package com.rizkyjayusman.chat.document.query;

import com.rizkyjayusman.chat.document.ChatMessage;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashSet;

public class ChatMessageQuery extends SearchQuery {

    public ChatMessageQuery() {
        criterion = new HashSet<>();
    }

    public ChatMessageQuery chatRoomUid(String chatRoomUid) {
        if (chatRoomUid != null) {
            criterion.add(Criteria.where(ChatMessage.Fields.roomUid).is(chatRoomUid));
        }

        return this;
    }

    public ChatMessageQuery recipient(String recipient) {
        if (recipient != null) {
            criterion.add(Criteria.where(ChatMessage.Fields.readByList)
                    .elemMatch(Criteria.where(ChatMessage.ReadChatStatusDto.Fields.recipient).is(recipient)));
        }

        return this;
    }

    public ChatMessageQuery unread(Boolean isUnread) {
        if (Boolean.TRUE.equals(isUnread)) {
            criterion.add(Criteria.where(ChatMessage.Fields.readByList).
                    elemMatch(Criteria.where(ChatMessage.ReadChatStatusDto.Fields.readDate).exists(false)
                            .orOperator(Criteria.where(ChatMessage.ReadChatStatusDto.Fields.readDate).isNull())));
        }

        return this;
    }
}
