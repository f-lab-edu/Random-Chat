package com.example.ranchat.userchatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ranchat.userchatroom.entity.UserChatRoom;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

}
