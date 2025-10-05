package com.hotpack.krocs.domain.retrospectives.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.FactorDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import com.hotpack.krocs.domain.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RetrospectiveConverterTest {

    private RetrospectiveConverter retrospectiveConverter;
    private User validUser;
    private Goal validGoal;

    @BeforeEach
    void setUp() {
        retrospectiveConverter = new RetrospectiveConverter();
        validUser = User.builder().userId(1L).build();
        validGoal = Goal.builder()
            .goalId(10L)
            .title("테스트 목표")
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 12, 31))
            .isCompleted(false)
            .build();
    }

    @Nested
    @DisplayName("toEntity 메소드 테스트")
    class ToEntityTests {

        @Test
        @DisplayName("성공: 일반적인 요청을 Retrospective 엔티티로 변환")
        void toEntity_Success() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of("CLEAR_PLAN"))
                .build();

            // when
            Retrospective result = retrospectiveConverter.toEntity(request, validUser, validGoal);

            // then
            assertThat(result.getUser()).isEqualTo(validUser);
            assertThat(result.getGoal()).isEqualTo(validGoal);
            assertThat(result.getOutcome()).isEqualTo(RetrospectiveOutcome.COMPLETE_SUCCESS);
            assertThat(result.getFactors()).containsExactly("CLEAR_PLAN");
            assertThat(result.getContent()).isNull();
        }

        @Test
        @DisplayName("성공: ETC를 포함한 요청 시 content가 올바르게 저장됨")
        void toEntity_Success_WithEtcAndContent() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("RETRY_FAILURE")
                .factors(List.of("NO_PLAN", "ETC"))
                .content("개인적인 사정이 있었음")
                .build();

            // when
            Retrospective result = retrospectiveConverter.toEntity(request, validUser, validGoal);

            // then
            assertThat(result.getOutcome()).isEqualTo(RetrospectiveOutcome.RETRY_FAILURE);
            assertThat(result.getFactors()).contains("NO_PLAN", "ETC");
            assertThat(result.getContent()).isEqualTo("개인적인 사정이 있었음");
        }

        @Test
        @DisplayName("성공: ETC를 포함하지 않은 요청 시 content는 무시되고 null로 저장됨")
        void toEntity_Success_ContentIgnoredWithoutEtc() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(List.of("CLEAR_PLAN"))
                .content("이 내용은 무시되어야 함")
                .build();

            // when
            Retrospective result = retrospectiveConverter.toEntity(request, validUser, validGoal);

            // then
            assertThat(result.getContent()).isNull();
        }

        @Test
        @DisplayName("실패: factors가 null일 경우 예외 발생")
        void toEntity_Fail_FactorsNull() {
            // given
            RetrospectiveCreateRequestDTO request = RetrospectiveCreateRequestDTO.builder()
                .outcome("COMPLETE_SUCCESS")
                .factors(null)
                .build();

            // when & then
            assertThrows(RetrospectiveException.class,
                () -> retrospectiveConverter.toEntity(request, validUser, validGoal));
        }
    }

    @Nested
    @DisplayName("toCreateResponseDTO 메소드 테스트")
    class ToCreateResponseDTOTests {

        @Test
        @DisplayName("성공: Retrospective 엔티티를 응답 DTO로 변환")
        void toCreateResponseDTO_Success() {
            // given
            Retrospective retro = Retrospective.builder()
                .retrospectiveId(100L)
                .user(validUser)
                .goal(validGoal)
                .outcome(RetrospectiveOutcome.COMPLETE_SUCCESS)
                .factors(List.of("CLEAR_PLAN", "QUICK_PROGRESS"))
                .content(null)
                .build();

            RetrospectiveConverter converter = new RetrospectiveConverter();

            // when
            RetrospectiveCreateResponseDTO result = converter.toCreateResponseDTO(retro);

            // then
            assertThat(result.getRetrospectiveId()).isEqualTo(100L);
            assertThat(result.getGoalId()).isEqualTo(10L);
            assertThat(result.getOutcome()).isEqualTo(RetrospectiveOutcome.COMPLETE_SUCCESS);
            assertThat(result.getContent()).isNull();

            List<FactorDTO> resultFactors = result.getFactors();
            assertThat(resultFactors).hasSize(2);
            assertThat(resultFactors.get(0).getKey()).isEqualTo("CLEAR_PLAN");
        }

        @Test
        @DisplayName("실패: 유효하지 않은 Factor Key 포함 시 예외 발생")
        void toCreateResponseDTO_Fail_InvalidFactorKey() {
            // given
            Retrospective retroWithInvalidKey = Retrospective.builder()
                .outcome(RetrospectiveOutcome.COMPLETE_SUCCESS)
                .factors(List.of("INVALID_KEY"))
                .goal(validGoal)
                .build();

            RetrospectiveConverter converter = new RetrospectiveConverter();

            // when & then
            RetrospectiveException exception = assertThrows(
                RetrospectiveException.class,
                () -> converter.toCreateResponseDTO(retroWithInvalidKey)
            );

            assertThat(exception.getErrorCode())
                .isEqualTo(RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME);
        }
    }
}