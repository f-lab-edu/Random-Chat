package com.example.ranchat.user.controller;

import com.example.ranchat.annotation.LoginUser;
import com.example.ranchat.user.dto.JoinDTO;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;


    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinDTO joinDTO) {
        userService.join(joinDTO);
        return "ok";
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
