package com.example.ranchat.user.repository;

import com.example.ranchat.user.entity.User;

public interface UserRepository {
	Boolean existsByUsername(String username);

	User findByUsername(String username);

	User save(User user);

	User findById(Long userId);
}
