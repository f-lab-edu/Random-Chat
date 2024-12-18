package com.example.ranchat.user.repository;

import java.util.Optional;

import com.example.ranchat.user.entity.User;

public interface UserRepository {
	Boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

	User save(User user);

	Optional<User> findById(Long userId);
}
