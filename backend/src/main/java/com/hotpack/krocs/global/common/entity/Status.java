package com.hotpack.krocs.global.common.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @JsonValue
    public String getValue() {
        return status;
    }
}
