package com.example.ranchat.user.repository;

import com.example.ranchat.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자 이름으로 사용자 존재 여부 확인 테스트")
    @Test
    void testExistsByUsername() {
        //given
        User user = User.builder()
                .username("testUser")
                .password("password123")
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        //when
        Boolean isExist = userRepository.existsByUsername("testUser");

        //then
        assertThat(isExist).isTrue();
     }
    @DisplayName("사용자 이름으로 사용자 조회")
    @Test
    void test() {
        //given
        User user = User.builder()
             .username("testUser")
             .password("password123")
             .role("ROLE_USER")
             .build();
        userRepository.save(user);

        //when
        User foundUser = userRepository.findByUsername("testUser");

        //then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
        assertThat(foundUser.getPassword()).isEqualTo("password123");


      }
}