package com.example.ranchat.user.repository;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.ranchat.user.entity.User;

@Repository("UserRepositoryJpaImpl")
@Primary
public class UserRepositoryJpaImpl implements UserRepository {
	private final UserJpaRepository userJpaRepository;

	public UserRepositoryJpaImpl(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	@Override
	public Boolean existsByUsername(String username) {
		return userJpaRepository.existsByUsername(username);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userJpaRepository.findByUsername(username);
	}

	@Override
	public User save(User user) {
		userJpaRepository.save(user);
		return user;
	}

	@Override
	public Optional<User> findById(Long userId) {
		return userJpaRepository.findById(userId);
	}
}
