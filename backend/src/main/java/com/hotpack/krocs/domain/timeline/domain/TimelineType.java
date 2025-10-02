package com.hotpack.krocs.domain.timeline.domain;

import lombok.Getter;

@Getter
public enum TimelineType {
    PLAN("plan"),
    SUBGOAL("subgoal");

    private final String value;

    TimelineType(String value) {
        this.value = value;
    }

    public static TimelineType fromValue(String value) {
        for (TimelineType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}