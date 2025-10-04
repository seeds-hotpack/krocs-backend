package com.hotpack.krocs.domain.retrospectives.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FailureFactor {
    NO_PLAN("계획을 안 세움"),
    WRONG_ORDER("순서를 잘못 정함"),
    LACK_OF_FOCUS("집중을 못함"),
    BURNOUT("너무 몰아서 하다가 지침"),
    MORE_TIME_NEEDED("예상보다 시간이 더 필요함"),
    UNEXPECTED_EVENT("갑자기 생긴 일"),
    ETC("기타 (직접 작성)");

    private final String description;

    public static boolean isValidKey(String key) {
        return Arrays.stream(FailureFactor.values())
            .anyMatch(factor -> factor.name().equals(key));
    }
}
