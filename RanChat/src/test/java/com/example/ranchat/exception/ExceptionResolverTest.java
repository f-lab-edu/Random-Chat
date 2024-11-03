package com.example.ranchat.exception;

import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
// 테스트 좀요 10/23
public class ExceptionResolverTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    public void setUp() {
        User existingUser = User.builder()
                .username("existingUser")
                .password("password")
                .role("ROLE_USER")
                .build();
        userJpaRepository.save(existingUser);
    }

//    @Test
//    public void testNotFoundUserExceptionHandler() throws Exception {
//        mockMvc.perform(get("/api/user/nonexistent"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
//                .andExpect(jsonPath("$.errorMessage").value("User not found"));
//    }

    @Test
    public void testValidationExceptionHandler() throws Exception {
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.errorMessage").isArray());
    }

    @Test
    @DisplayName("UsernameDuplicationException 예외 테스트")
    public void UsernameDuplicationExceptionTest() throws Exception {
        String userJson = "{ \"username\": \"existingUser\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)) // JSON 형식의 회원 정보를 본문에 추가
                .andExpect(status().isConflict()) // 중복된 경우 상태 코드 409 (Conflict) 반환
                .andExpect(jsonPath("$.errorCode").value("NOT_ALLOWED")) // 예외 응답 필드 검증
                .andExpect(jsonPath("$.message").value("이미 존재하는 username 입니다.")); // 예외 메시지 검증

    }

    // 추가 테스트 케이스
}