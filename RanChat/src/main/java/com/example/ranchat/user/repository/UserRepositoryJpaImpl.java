package com.example.ranchat.user.repository;

import com.example.ranchat.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository("UserRepositoryJpaImpl")
public class UserRepositoryJpaImpl implements UserRepository{
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryJpaImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        userJpaRepository.save(user);
        return user;
    }
}
