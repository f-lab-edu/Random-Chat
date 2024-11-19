package com.example.ranchat.chatroom.entity.userchatroom;

import com.example.ranchat.BaseEntity;
import com.example.ranchat.chatroom.entity.ChatRoom;
import com.example.ranchat.user.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class UserChatRoom extends BaseEntity{
    @EmbeddedId
    private UserChatRoomId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userKey")
    @JoinColumn(name = "user_key")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomKey")
    @JoinColumn(name = "chatroom_key")
    private ChatRoom chatRoom;

}
