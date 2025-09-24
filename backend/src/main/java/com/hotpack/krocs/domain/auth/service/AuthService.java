package com.hotpack.krocs.domain.auth.service;

import com.hotpack.krocs.domain.auth.dto.response.UserResponseDTO;
import com.hotpack.krocs.domain.auth.exception.AuthException;
import com.hotpack.krocs.domain.auth.exception.AuthExceptionType;
import com.hotpack.krocs.domain.user.converter.UserConverter;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final RedisTokenService redisTokenService;
    private final UserRepositoryFacade userRepositoryFacade;

    public ResponseCookie expireAuthTokenCookie() {
        return ResponseCookie.from("AUTH-TOKEN", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build();
    }


    public UserResponseDTO getUser(Long userId) {
        User user = userRepositoryFacade.findActiveUserByUserId(userId);
        if (user == null) {
            throw new AuthException(AuthExceptionType.AUTH_USER_NOT_FOUND);
        }

        return UserConverter.toUserResponseDTO(user);
    }
}
