package com.example.ranchat;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.ranchat.annotation.LoginUser;
import com.example.ranchat.jwt.JWTParser;
import com.example.ranchat.user.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

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
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String authorization = webRequest.getHeader("Authorization");
		String token = authorization.split(" ")[1];
		String username = jwtParser.getUsername(token);
		return userJpaRepository.findByUsername(username).get();
	}
}
