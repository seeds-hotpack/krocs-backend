package com.hotpack.krocs.domain.plans.validator;

import com.hotpack.krocs.domain.plans.domain.PlanCategory;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanValidator {

    public void validatePlanCreation(PlanCreateRequestDTO requestDTO, Long subGoalId) {
        Boolean allDay = requestDTO.getAllDay();
        if (allDay == null) {
            allDay = false;
        }

        validateTitle(requestDTO.getTitle());
        validatePlanCategory(requestDTO.getPlanCategory());
        validateSubGoalIdParameter(subGoalId);
        validateAllDayDateTime(allDay, requestDTO.getStartDateTime(), requestDTO.getEndDateTime());
    }

    public void validateGetPlan(Long planId) {
        validatePlanIdParameter(planId);
    }

    public void validateUpdatePlan(Long planId) {
        validatePlanIdParameter(planId);
    }

    public void validateDeletePlan(Long planId) {
        validatePlanIdParameter(planId);
    }

    private void validatePlanIdParameter(Long planId) {
        if (planId == null || planId <= 0) {
            throw new PlanException(PlanExceptionType.PLAN_INVALID_PLAN_ID);
        }
    }

    public void validateSubGoalIdParameter(Long subGoalId) {
        if (subGoalId == null) return;
        if (subGoalId <= 0) {
            throw new PlanException(PlanExceptionType.PLAN_INVALID_GOAL_ID);
        }
    }

    public void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new PlanException(PlanExceptionType.PLAN_TITLE_EMPTY);
        }
        if (title.length() > 200) {
            throw new PlanException(PlanExceptionType.PLAN_TITLE_TOO_LONG);
        }
    }

    public void validatePlanCategory(PlanCategory planCategory){
        if(planCategory == null){
            throw new PlanException(PlanExceptionType.PLAN_PLANCATEGORY_IS_NULL);
        }
    }

    public void validateAllDayDateTime(Boolean allDay, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null) {
            throw new PlanException(PlanExceptionType.PLAN_START_TIME_REQUIRED);
        }

        if (endDateTime == null) {
            throw new PlanException(PlanExceptionType.PLAN_END_TIME_REQUIRED);
        }

        if (allDay) {
            LocalDate startDate = startDateTime.toLocalDate();
            LocalDate endDate = endDateTime.toLocalDate();

            if (startDate.isAfter(endDate)) {
                throw new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE);
            }
        } else {
            if (startDateTime.isAfter(endDateTime)) {
                throw new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE);
            }
        }
    }

    public void validateDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new PlanException(PlanExceptionType.INVALID_PLAN_DATE_RANGE);
        }
    }
}
