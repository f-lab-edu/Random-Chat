package com.example.ranchat.user.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ranchat.user.dto.CustomUserDetails;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserJpaRepository userJpaRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//DB에서 조회
		Optional<User> userData = userJpaRepository.findByUsername(username);

		if (userData.isPresent()) {
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
			return new CustomUserDetails(userData.get());
		}
		return null;
	}
}
