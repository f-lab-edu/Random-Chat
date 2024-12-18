package com.example.ranchat.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ranchat.user.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
	Boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

}
