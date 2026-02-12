package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalValidator {

    public void validateGoalCreation(GoalCreateRequestDTO requestDTO) {
        validateTitle(requestDTO.getTitle());
        validateDateRange(requestDTO.getStartDate(), requestDTO.getEndDate());
    }

    public void validateGoalIdParameter(Long goalId) {
        if (goalId == null) {
            throw new GoalException(GoalExceptionType.GOAL_INVALID_GOAL_ID);
        }
        if (goalId <= 0) {
            throw new GoalException(GoalExceptionType.GOAL_INVALID_GOAL_ID);
        }
    }

    public void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new GoalException(GoalExceptionType.GOAL_TITLE_EMPTY);
        }
        if (title.length() > 200) {
            throw new GoalException(GoalExceptionType.GOAL_TITLE_TOO_LONG);
        }
    }

    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return;
        }
        if (startDate.isAfter(endDate)) {
            throw new GoalException(GoalExceptionType.INVALID_GOAL_DATE_RANGE);
        }
    }

    public void validateMonthlyGoalRequest(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new GoalException(GoalExceptionType.GOAL_INVALID_REQUEST);
        }

        if (year < 2000 || year > 3000) {
            throw new GoalException(GoalExceptionType.GOAL_INVALID_YEAR);
        }

        if (month < 1 || month > 12) {
            throw new GoalException(GoalExceptionType.GOAL_INVALID_MONTH);
        }
    }
}
