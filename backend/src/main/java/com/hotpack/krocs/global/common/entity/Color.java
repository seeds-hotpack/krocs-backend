package com.hotpack.krocs.global.common.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Color {
    PLAN_BLUE("#2196f3"),      // 파랑 (기본값)
    PLAN_RED("#F44336"),       // 빨강
    PLAN_GREEN("#4caf50"),     // 초록
    PLAN_PURPLE("#9c27b0"),    // 보라
    PLAN_ORANGE("#ff9800"),    // 주황
    PLAN_YELLOW("#ffeb3b"),    // 노랑
    PLAN_NAVY("#607d8b"),

    GOAL_BLUE("#bbdefb"),      // 파랑 (기본값)
    GOAL_RED("#ffcdd2"),       // 빨강
    GOAL_GREEN("#c8e6c9"),     // 초록
    GOAL_PURPLE("#e1bee7"),    // 보라
    GOAL_ORANGE("#ffe0b2"),    // 주황
    GOAL_YELLOW("#fff9c4"),    // 노랑
    GOAL_NAVY("#BDBDBD");      // 남색

    private final String hex;

    Color(String hex) {
        this.hex = hex;
    }

    @JsonValue
    public String getHex() {
        return hex;
    }
}
