package com.hotpack.krocs.domain.auth.dto.response;

import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {

    private Long userId;

    private String name;

    private String email;

    private AccountType accountType;
}
