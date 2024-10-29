package com.example.ranchat;

import com.example.ranchat.annotation.LoginUser;
import com.example.ranchat.jwt.JWTUtil;
import com.example.ranchat.user.entity.User;
import com.example.ranchat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
@RequiredArgsConstructor
@Component
public class LoginUserResolver implements HandlerMethodArgumentResolver {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String Authorization = webRequest.getHeader("Authorization");

        if (Authorization == null) {
            // 예외 만드쇼
            // throw new ApiException(ErrorStatus._EMPTY_JWT);
        }

        String token = Authorization.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        // 아 이거 Optional로 처리해야하나??
        User user = userRepository.findByUsername(username);

        return user;
    }
}
