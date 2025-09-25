package com.hotpack.krocs.domain.user.converter;

import com.hotpack.krocs.domain.auth.dto.response.UserResponseDTO;
import com.hotpack.krocs.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public static UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .accountType(user.getAccountType())
            .build();
    }
}
