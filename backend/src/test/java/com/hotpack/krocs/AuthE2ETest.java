package com.hotpack.krocs;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.profiles.active=dev")
@AutoConfigureMockMvc
class AuthE2ETest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_TOKEN = "dev-fixed-token";

    @BeforeEach
    void setup() {
        // 단순 문자열 JSON 저장으로 직렬화 이슈 제거
        String json = "{\"userId\":\"u-123\",\"displayName\":\"Dev User\",\"roles\":[\"USER\"]}";
        stringRedisTemplate.opsForValue()
            .set("auth:token:" + TEST_TOKEN, json, Duration.ofHours(1));
    }

    @Test
    void 인증_성공시_me_조회() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + TEST_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.result.userId").value("u-123"));
    }

    @Test
    void 토큰_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("GLOBAL401"));
    }
}

