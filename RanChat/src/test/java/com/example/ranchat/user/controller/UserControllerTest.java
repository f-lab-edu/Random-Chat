package com.example.ranchat.user.controller;

import com.example.ranchat.LoginUserResolver;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import com.example.ranchat.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .username("doncham")
                .password("1234567")
                .role("USER")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

    }


    @DisplayName("회원가입 성공 테스트")
    @WithMockUser("user1")
    @Test
    void JoinSuccessTest() throws Exception{
        //given
        JoinDTO joinDTO = new JoinDTO("상원", "12345678");
        doNothing().when(userService).join(any(JoinDTO.class));

        //when then
        mockMvc.perform(post("/api/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDTO)))

                // Then: 응답이 HTTP 200 OK이고, "ok"라는 문자열을 반환해야 한다.
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

     }

    @DisplayName("회원가입 username NotEmpty 유효성 검증 테스트")
    @WithMockUser("user1")
    @Test
    void JoinUsernameNullTest() throws Exception{
        //given
        JoinDTO joinDTO = new JoinDTO(null, "12345678");
        doNothing().when(userService).join(any(JoinDTO.class));

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
        doNothing().when(userService).join(any(JoinDTO.class));

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
}