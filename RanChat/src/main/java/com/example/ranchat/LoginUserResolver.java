package com.example.ranchat;

import com.example.ranchat.annotation.LoginUser;
import com.example.ranchat.jwt.JWTParser;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
@RequiredArgsConstructor
@Component
public class LoginUserResolver implements HandlerMethodArgumentResolver {
    private final JWTParser jwtParser;
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        String username = jwtParser.getUsername(token);
        // 아 이거 Optional로 처리해야하나??
        User user = userJpaRepository.findByUsername(username);

        return user;
    }
}
