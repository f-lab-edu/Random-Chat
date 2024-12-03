package com.example.ranchat.chatroom.entity.userchatroom;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserChatRoomId implements Serializable {
    private Long userKey;
    private Long chatRoomKey;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UserChatRoomId that = (UserChatRoomId) object;
        return Objects.equals(userKey, that.userKey) && Objects.equals(chatRoomKey, that.chatRoomKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userKey, chatRoomKey);
    }
}
