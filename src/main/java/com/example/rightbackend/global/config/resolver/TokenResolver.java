package com.example.rightbackend.global.config.resolver;

import com.example.rightbackend.auth.controller.dto.LoginMember;
import com.example.rightbackend.auth.service.TokenProvider;
import com.example.rightbackend.global.exception.RestApiException;
import com.example.rightbackend.global.response.error.TokenError;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenResolver implements HandlerMethodArgumentResolver {

    private final TokenProvider tokenProvider;

    public TokenResolver(final TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LoginMember.class) && parameter.hasParameterAnnotation(Login.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        String Token = webRequest.getHeader("Authorization");
        if (Token == null || Token.trim().isEmpty()) {
            throw new RestApiException(TokenError.MISSING_CREDENTIALS);
        }
        return tokenProvider.getLoginFromToken(Token);
    }
}