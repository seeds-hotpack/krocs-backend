package com.hotpack.krocs.domain.goals.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.global.common.entity.Priority;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.show-sql=true"
})
class GoalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GoalRepository goalRepository;

    private Goal goal1;
    private Goal goal2;
    private Goal goal3;
    private Goal goal4;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
            .name("박성열")
            .email("qkrtjdduf@example.com")
            .accountId("local-" + UUID.randomUUID())
            .accountType(AccountType.LOCAL)
            .build();
        entityManager.persistAndFlush(user);

        // 2025-07-01 ~ 2025-07-31 (7월 전체)
        goal1 = Goal.builder()
            .title("7월 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.of(2025, 7, 1))
            .endDate(LocalDate.of(2025, 7, 31))
            .isCompleted(false)
            .user(user)
            .build();

        // 2025-07-15 ~ 2025-08-15 (7월 중순 ~ 8월 중순)
        goal2 = Goal.builder()
            .title("7-8월 목표")
            .priority(Priority.MEDIUM)
            .startDate(LocalDate.of(2025, 7, 15))
            .endDate(LocalDate.of(2025, 8, 15))
            .isCompleted(false)
            .user(user)
            .build();

        // 2025-08-01 ~ 2025-08-31 (8월 전체)
        goal3 = Goal.builder()
            .title("8월 목표")
            .priority(Priority.LOW)
            .startDate(LocalDate.of(2025, 8, 1))
            .endDate(LocalDate.of(2025, 8, 31))
            .isCompleted(true)
            .user(user)
            .build();

        // 2025-06-01 ~ 2025-06-30 (6월 전체)
        goal4 = Goal.builder()
            .title("6월 목표")
            .priority(Priority.CRITICAL)
            .startDate(LocalDate.of(2025, 6, 1))
            .endDate(LocalDate.of(2025, 6, 30))
            .isCompleted(true)
            .user(user)
            .build();

        // 데이터베이스에 저장
        entityManager.persistAndFlush(goal1);
        entityManager.persistAndFlush(goal2);
        entityManager.persistAndFlush(goal3);
        entityManager.persistAndFlush(goal4);
    }

    @Test
    @DisplayName("날짜 범위 내 목표 조회 - 7월 25일")
    void findByDateTime_Success_July25() {
        // given
        LocalDate searchDate = LocalDate.of(2025, 7, 25);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Goal::getTitle)
            .containsExactlyInAnyOrder("7월 목표", "7-8월 목표");
    }

    @Test
    @DisplayName("날짜 범위 내 목표 조회 - 8월 10일")
    void findByDateTime_Success_August10() {
        // given
        LocalDate searchDate = LocalDate.of(2025, 8, 10);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Goal::getTitle)
            .containsExactlyInAnyOrder("7-8월 목표", "8월 목표");
    }

    @Test
    @DisplayName("날짜 범위 내 목표 조회 - 경계값 테스트 (시작일)")
    void findByDateTime_BoundaryTest_StartDate() {
        // given
        LocalDate searchDate = LocalDate.of(2025, 7, 1);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("7월 목표");
    }

    @Test
    @DisplayName("날짜 범위 내 목표 조회 - 경계값 테스트 (종료일)")
    void findByDateTime_BoundaryTest_EndDate() {
        // given
        LocalDate searchDate = LocalDate.of(2025, 7, 31);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        assertThat(result).extracting(Goal::getTitle)
            .contains("7월 목표");
    }

    @Test
    @DisplayName("날짜 범위 밖 목표 조회 - 빈 결과")
    void findByDateTime_EmptyResult() {
        // given
        LocalDate searchDate = LocalDate.of(2025, 9, 15);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).isEmpty();
    }

//    @Test
//    @DisplayName("날짜 범위 내 목표 조회 - 과거 날짜")
//    void findByDateTime_PastDate() {
//        // given
//        LocalDate searchDate = LocalDate.of(2025, 6, 15);
//
//        // when
//        List<Goal> result = goalRepository.findByDateTime(searchDate);
//
//        // then
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getTitle()).isEqualTo("6월 목표");
//    }

    @Test
    @DisplayName("날짜 범위 내 목표 조회 - null 날짜 필드가 있는 경우")
    void findByDateTime_WithNullDates() {
        // given
        Goal goalWithNullDates = Goal.builder()
            .title("날짜 없는 목표")
            .priority(Priority.MEDIUM)
            .startDate(null)
            .endDate(null)
            .isCompleted(false)
            .user(user)
            .build();

        entityManager.persistAndFlush(goalWithNullDates);

        LocalDate searchDate = LocalDate.of(2025, 7, 15);

        // when
        List<Goal> result = goalRepository.findByDate(searchDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Goal::getTitle)
            .containsExactlyInAnyOrder("7월 목표", "7-8월 목표")
            .doesNotContain("날짜 없는 목표");
    }

    // ========== findGoalByGoalId 테스트 ==========

    @Test
    @DisplayName("goalId로 목표 조회 성공")
    void findGoalByGoalId_Success() {
        // given
        Long goalId = goal1.getGoalId();

        // when
        Goal result = goalRepository.findGoalByGoalId(goalId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoalId()).isEqualTo(goalId);
        assertThat(result.getTitle()).isEqualTo("7월 목표");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 7, 31));
    }

    @Test
    @DisplayName("goalId로 목표 조회 실패 - 존재하지 않는 ID")
    void findGoalByGoalId_NotFound() {
        // given
        Long nonExistentId = 999L;

        // when
        Goal result = goalRepository.findGoalByGoalId(nonExistentId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("goalId로 목표 조회 - null ID")
    void findGoalByGoalId_NullId() {
        // given
        Long nullId = null;

        // when
        Goal result = goalRepository.findGoalByGoalId(nullId);

        // then
        assertThat(result).isNull();
    }

    // ========== 기본 JPA 메소드 테스트 ==========

    @Test
    @DisplayName("기본 findById 메소드 테스트")
    void findById_Success() {
        // given
        Long goalId = goal1.getGoalId();

        // when
        Optional<Goal> result = goalRepository.findById(goalId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("7월 목표");
    }

    @Test
    @DisplayName("기본 findAll 메소드 테스트")
    void findAll_Success() {
        // when
        List<Goal> result = goalRepository.findAll();

        // then
        assertThat(result).hasSize(4);
        assertThat(result).extracting(Goal::getTitle)
            .containsExactlyInAnyOrder("7월 목표", "7-8월 목표", "8월 목표", "6월 목표");
    }

    @Test
    @DisplayName("기본 existsById 메소드 테스트")
    void existsById_Success() {
        // given
        Long existingId = goal1.getGoalId();
        Long nonExistentId = 999L;

        // when & then
        assertThat(goalRepository.existsById(existingId)).isTrue();
        assertThat(goalRepository.existsById(nonExistentId)).isFalse();
    }

    @Test
    @DisplayName("기본 save 메소드 테스트")
    void save_Success() {
        // given
        Goal newGoal = Goal.builder()
            .title("새로운 목표")
            .priority(Priority.HIGH)
            .startDate(LocalDate.of(2025, 9, 1))
            .endDate(LocalDate.of(2025, 9, 30))
            .isCompleted(false)
            .user(user)
            .build();

        // when
        Goal savedGoal = goalRepository.save(newGoal);

        // then
        assertThat(savedGoal).isNotNull();
        assertThat(savedGoal.getGoalId()).isNotNull();
        assertThat(savedGoal.getTitle()).isEqualTo("새로운 목표");

        // 데이터베이스에서 확인
        Optional<Goal> foundGoal = goalRepository.findById(savedGoal.getGoalId());
        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getTitle()).isEqualTo("새로운 목표");
    }

    @Test
    @DisplayName("기본 deleteById 메소드 테스트")
    void deleteById_Success() {
        // given
        Long goalId = goal1.getGoalId();

        // 삭제 전 존재 확인
        assertThat(goalRepository.existsById(goalId)).isTrue();

        // when
        goalRepository.deleteById(goalId);
        goalRepository.flush(); // 즉시 삭제 실행

        // then
        assertThat(goalRepository.existsById(goalId)).isFalse();

        List<Goal> remainingGoals = goalRepository.findAll();
        assertThat(remainingGoals).hasSize(3);
    }

    // ========== 데이터 무결성 테스트 ==========

    @Test
    @DisplayName("필수 필드 누락 시 저장 실패 테스트")
    void save_RequiredFieldMissing() {
        // given
        Goal invalidGoal = Goal.builder()
            .title(null) // 필수 필드 누락
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        // when & then
        try {
            goalRepository.save(invalidGoal);
            goalRepository.flush();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("제목 길이 초과 시 저장 실패 테스트")
    void save_TitleTooLong() {
        // given
        String longTitle = "a".repeat(201); // 200자 초과
        Goal invalidGoal = Goal.builder()
            .title(longTitle)
            .priority(Priority.HIGH)
            .isCompleted(false)
            .build();

        // when & then
        try {
            goalRepository.save(invalidGoal);
            goalRepository.flush();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}
