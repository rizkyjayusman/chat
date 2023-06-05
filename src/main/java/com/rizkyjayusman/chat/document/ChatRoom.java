package com.rizkyjayusman.chat.document;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter @Setter
@Document(collection = "chat_rooms")
public class ChatRoom {

    @Id
    private ObjectId id;
    private String uid;
    private String title;
    private List<String> members;

}
