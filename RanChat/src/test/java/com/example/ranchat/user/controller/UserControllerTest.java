package com.example.ranchat.user.controller;

import com.example.ranchat.LoginUserResolver;
import com.example.ranchat.exception.UsernameDuplicationException;
import com.example.ranchat.response.ResponseCode;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.repository.UserRepository;
import com.example.ranchat.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private LoginUserResolver loginUserResolver;

    @MockBean
    private UserRepository userRepository;




    @DisplayName("회원가입 성공 테스트")
    @WithMockUser("user1")
    @Test
    void JoinSuccessTest() throws Exception{
        //given
        JoinDTO joinDTO = new JoinDTO("상원", "12345678");

        when(userService.join(Mockito.any(JoinDTO.class)))
                .thenReturn(ResponseEntity.status(201).body("상원 created"));

        //when then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDTO)))

                // Then: 응답이 HTTP 200 OK이고, "ok"라는 문자열을 반환해야 한다.
                .andExpect(status().isCreated())
                .andExpect(content().string(joinDTO.getUsername() + " created"));



     }

    @DisplayName("회원가입 username NotEmpty 유효성 검증 테스트")
    @WithMockUser("user1")
    @Test
    void JoinUsernameNullTest() throws Exception{
        //given
        JoinDTO joinDTO = new JoinDTO(null, "12345678");
        when(userService.join(Mockito.any(JoinDTO.class)))
                .thenReturn(ResponseEntity.status(400).body("Username cannot be empty"));

        //when then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDTO)))

                // Then: 응답이 HTTP 200 OK이고, "ok"라는 문자열을 반환해야 한다.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username cannot be empty"));
    }

    @DisplayName("회원가입 username NotEmpty 유효성, password NotEmpty 유효성 검증 테스트")
    @WithMockUser("user1")
    @Test
    void JoinUsernameNullPasswordNullTest() throws Exception{
        //given
        JoinDTO joinDTO = new JoinDTO(null, null);

        Map<String, Object> response = new HashMap<>();
        response.put("message", List.of("Username cannot be empty", "Password cannot be empty"));

        when(userService.join(Mockito.any(JoinDTO.class)))
                .thenReturn(ResponseEntity.status(400).<String>body(response.toString()));

        //when then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDTO)))

                // Then: 응답이 HTTP 200 OK이고, "ok"라는 문자열을 반환해야 한다.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", hasSize(2)))
                .andExpect(jsonPath("$.message[0]").value("Username cannot be empty"))
                .andExpect(jsonPath("$.message[1]").value("Password cannot be empty"));
    }
    @DisplayName("이미 존재하는 username이면 회원가입 시 발생한 예외를 exceptionHandler가 잘 처리")
    @WithMockUser("user1")
    @Test
    void joinShouldReturnConflictWhenUsernameExists() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("existingUser", "password123");
        when(userService.join(any(JoinDTO.class)))
                .thenThrow(new UsernameDuplicationException(ResponseCode.NOT_ALLOWED, "이미 존재하는 username 입니다."));

        // when & then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDTO)))
                .andExpect(status().isConflict())  // HTTP 409 상태 코드
                .andExpect(jsonPath("$.message").value("이미 존재하는 username 입니다."));
    }
}