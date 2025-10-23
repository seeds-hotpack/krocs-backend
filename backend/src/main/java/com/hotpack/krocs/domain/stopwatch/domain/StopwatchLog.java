package com.hotpack.krocs.domain.stopwatch.domain;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stopwatch_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopwatchLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stopwatch_log_id")
    private Long stopwatchLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_goal_id", nullable = false)
    private SubGoal subGoal;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "elapsed_time", nullable = false, length = 10)
    private String elapsedTime;
}
