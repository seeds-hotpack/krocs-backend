package com.hotpack.krocs.domain.goals.repository;

import java.time.LocalDate;

public interface DailyGoalCountProjection {
    LocalDate getDate();

    Integer getGoalCount();
}
