package com.example.ranchat.user.service;

import com.example.ranchat.exception.UsernameDuplicationException;
import com.example.ranchat.response.ResponseCode;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public ResponseEntity<String> join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            throw new UsernameDuplicationException(ResponseCode.NOT_ALLOWED, "이미 존재하는 username 입니다.");
        }

        User user = User.builder()
                .username(joinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        return new ResponseEntity<>(username + " created", HttpStatus.CREATED);
    }
}
