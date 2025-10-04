package com.hotpack.krocs.domain.retrospectives.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessFactor {
    CLEAR_PLAN("명확한 계획"),
    ENOUGH_TIME("여유로운 시간"),
    TIME_MANAGEMENT_SUCCESS("시간 분배 성공"),
    STEADY_EXECUTION("꾸준한 실행력 유지"),
    GOOD_COOPERATION("동료/팀원과의 원활한 협력"),
    FEEDBACK_HABIT("기록 및 피드백 습관화"),
    QUICK_PROGRESS("예상보다 빠른 진행 속도"),
    NEW_CHALLENGE_SUCCESS("새로운 시도 및 도전 성공"),
    ETC("기타 (직접 작성)");

    private final String description;

    public static boolean isValidKey(String key) {
        return Arrays.stream(SuccessFactor.values())
            .anyMatch(factor -> factor.name().equals(key));
    }
}
