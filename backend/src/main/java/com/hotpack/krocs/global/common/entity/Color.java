package com.hotpack.krocs.global.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;
import lombok.Getter;

@Getter
public enum Color {
    PLAN_BLUE("#2196f3"),      // 파랑 (기본값)
    PLAN_RED("#f44336"),       // 빨강
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
    GOAL_NAVY("#bdbdbd");      // 남색

    private final String hex;

    Color(String hex) {
        this.hex = hex;
    }

    @JsonCreator
    public static Color fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (Color color : values()) {
            String hexLowerCase = color.hex.toLowerCase(Locale.ROOT);
            String nameLowerCase = color.name().toLowerCase(Locale.ROOT);
            if (hexLowerCase.equals(normalized) || nameLowerCase.equals(normalized)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color value: " + value);
    }

    @JsonValue
    public String getHex() {
        return hex;
    }
}
