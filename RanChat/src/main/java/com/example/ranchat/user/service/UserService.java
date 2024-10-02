package com.example.ranchat.user.service;

import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public void join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            return;
        }

        User user = User.builder()
                .username(joinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

    }
}
