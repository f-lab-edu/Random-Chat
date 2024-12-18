package com.example.ranchat.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ranchat.annotation.LoginUser;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@GetMapping("/test")
	public String test() {
		return "test ok";
	}

	@PostMapping("/join")
	public ResponseEntity<String> join(@Valid @RequestBody JoinDTO joinDTO) {
		return userService.join(joinDTO);
	}

	@GetMapping("/jwt-check")
	public String jwt() {
		return "jwtWorking";
	}

	@GetMapping("/argumentResolverTest")
	public void arg(@Valid @LoginUser User user) {
		System.out.println("argumentResolver said: " + user.getUsername());
	}

}
