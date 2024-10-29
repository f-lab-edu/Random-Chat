package com.example.ranchat.user.service;

import com.example.ranchat.exception.UsernameDuplicationException;
import com.example.ranchat.response.ResponseCode;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @DisplayName("회원가입 성공 로직 테스트")
    @Test
    void joinSuccessTest() {
        //given
        JoinDTO joinDTO = new JoinDTO("testUser", "password123");

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");

        //when
        userService.join(joinDTO);

        //then
        verify(userRepository).save(any(User.class));
     }

     @DisplayName("회원 이름 중복 시 예외 발생 테스트")
     @Test
     void joinShouldThrowExceptionWhenUsernameDuplicated() {
         //given
         JoinDTO joinDTO = new JoinDTO("existingUser", "password123");

         when(userRepository.existsByUsername("existingUser")).thenReturn(true);

         //when & then
         UsernameDuplicationException exception = assertThrows(UsernameDuplicationException.class, () -> {
             userService.join(joinDTO);
         });

         assertEquals(exception.getErrorCode(),ResponseCode.NOT_ALLOWED);
         assertEquals(exception.getMessage(), "이미 존재하는 username 입니다.");


     }

}