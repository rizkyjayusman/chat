package com.rizkyjayusman.chat.document;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@FieldNameConstants
@Getter @Setter
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private ObjectId id;
    private String uid;
    private String roomUid;
    private String sender;
    private List<String> recipients;
    private List<ReadChatStatusDto> readByList;
    private String content;
    private LocalDateTime timestamp;

    @FieldNameConstants
    @Getter @Setter
    public static class ReadChatStatusDto {
        private String recipient;
        private Date readDate;
    }

}
