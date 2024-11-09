package com.example.ranchat.user.service;

import com.example.ranchat.exception.UsernameDuplicationException;
import com.example.ranchat.response.ResponseCode;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("UserRepositoryJdbcImpl")
    private final UserRepository userRepositoryJpa;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public ResponseEntity<String> join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Boolean isExist = userRepositoryJpa.existsByUsername(username);

        if (isExist) {
            throw new UsernameDuplicationException(ResponseCode.DUPLICATED_USERNAME);
        }

        User user = User.builder()
                .username(joinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .build();

        userRepositoryJpa.save(user);

        return new ResponseEntity<>(username + " created", HttpStatus.CREATED);
    }
}
