package com.hotpack.krocs.domain.plans.domain;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.BaseTimeEntity;
import com.hotpack.krocs.global.common.entity.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plans", indexes = {
    @Index(name = "idx_plans_user_id", columnList = "user_id"),
    @Index(name = "idx_plans_goal_id", columnList = "goal_id"),
    @Index(name = "idx_plans_sub_goal_id", columnList = "sub_goal_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Plan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_goal_id")
    private SubGoal subGoal;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubPlan> subPlans;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_category", nullable = false)
    @Builder.Default
    private PlanCategory planCategory = PlanCategory.ETC;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    @Builder.Default
    private Color color = Color.BLUE;

    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    @Column(name = "all_day", nullable = false)
    @Builder.Default
    private Boolean allDay = false;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    // 미루기 기능
    // @Column(name = "is_snoozed", nullable = false)
    // @Builder.Default
    // private Boolean isSnoozed = false;

    // @Column(name = "snooze_until")
    // private LocalDateTime snoozeUntil;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public void updateFrom(PlanUpdateRequestDTO request, Goal goal, SubGoal subGoal) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }

        if (request.getPlanCategory() != null) {
            this.planCategory = request.getPlanCategory();
        }

        if (request.getColor() != null) {
            this.color = request.getColor();
        }

        if (request.getStartDateTime() != null) {
            this.startDateTime = request.getStartDateTime();
        }

        if (request.getEndDateTime() != null) {
            this.endDateTime = request.getEndDateTime();
        }

        if (request.getAllDay() != null) {
            this.allDay = request.getAllDay();
        }

        if (request.getIsCompleted() != null) {
            this.isCompleted = request.getIsCompleted();
            if (request.getIsCompleted()) {
                this.completedAt = LocalDateTime.now();
            } else {
                this.completedAt = null;
            }
        }

        if (goal != null) {
            this.goal = goal;
        }

        if (subGoal != null) {
            this.subGoal = subGoal;
        }
    }

    public void delete() {
        if (this.status == Status.ACTIVE) {
            this.status = Status.INACTIVE;
        }
    }

}