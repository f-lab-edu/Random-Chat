package com.example.ranchat.jwt;

import com.example.ranchat.user.dto.CustomUserDetails;
import com.example.ranchat.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginFilterTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private LoginFilter loginFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginFilter = new LoginFilter(authenticationManager, jwtUtil);
    }

    @DisplayName("successfulAuthentication - JWT 토큰이 헤더에 잘 들어가는지 확인")
    @Test
    void successfulAuthenticationTest() throws Exception{
        //given
        User user = User.builder()
                .username("user1")
                .password("password")
                .role("USER")
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // JWT 모킹
        when(jwtUtil.createJwt(customUserDetails.getUsername(), "USER", 60 * 60 * 100L)).thenReturn("mockJwtToken");

        //when
        loginFilter.successfulAuthentication(request, response, filterChain, authentication);

        //then
        verify(response).addHeader("Authorization", "Bearer mockJwtToken");
     }

     @DisplayName("unsuccessfulAuthentication - 인증 실패 시 상태 코드가 401인지 확인")
     @Test
     void unsuccessfulAuthenticationTest() throws Exception {
         // when
         loginFilter.unsuccessfulAuthentication(request, response, null);

         // then
         verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
     }

}