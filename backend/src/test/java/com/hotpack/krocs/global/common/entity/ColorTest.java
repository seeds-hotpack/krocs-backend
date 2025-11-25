package com.hotpack.krocs.global.common.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ColorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fromValue_HexString_Success() throws Exception {
        Color color = objectMapper.readValue("\"#f44336\"", Color.class);

        assertThat(color).isEqualTo(Color.PLAN_RED);
    }

    @Test
    void fromValue_EnumName_Success() throws Exception {
        Color color = objectMapper.readValue("\"plan_blue\"", Color.class);

        assertThat(color).isEqualTo(Color.PLAN_BLUE);
    }

    @Test
    void fromValue_InvalidValue_ThrowsException() {
        assertThatThrownBy(() -> Color.fromValue("#000000"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
