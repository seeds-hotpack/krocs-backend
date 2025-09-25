package com.hotpack.krocs.domain.goals.domain;

import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.global.common.entity.BaseTimeEntity;
import com.hotpack.krocs.global.common.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sub_goals")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubGoal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_goal_id")
    private Long subGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean isTimeSelected = false;

    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public void updateFrom(SubGoalUpdateRequestDTO requestDTO) {
        if (requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }

        if (requestDTO.getIsCompleted() != null) {
            this.isCompleted = requestDTO.getIsCompleted();
        }

        if (requestDTO.getStartDateTime() != null) {
            this.startDateTime = requestDTO.getStartDateTime();
        }

        if (requestDTO.getEndDateTime() != null) {
            this.endDateTime = requestDTO.getEndDateTime();
        }

        if (requestDTO.getIsTimeSelected() != null) {
            this.isTimeSelected = requestDTO.getIsTimeSelected();
            if (!this.isTimeSelected) {
                this.startDateTime = null;
                this.endDateTime = null;
            }
        }
    }

    public void delete() {
        if (this.status == Status.ACTIVE) {
            this.status = Status.INACTIVE;
        }
    }
}