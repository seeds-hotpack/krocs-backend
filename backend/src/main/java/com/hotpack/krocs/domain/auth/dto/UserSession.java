package com.hotpack.krocs.domain.auth.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSession implements Serializable {

    private final String userId;
    private final String name;

    public static UserSession of(String userId, String displayName) {
        return UserSession.builder()
            .userId(userId)
            .name(displayName)
            .build();
    }
}

