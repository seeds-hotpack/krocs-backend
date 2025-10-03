package com.hotpack.krocs.domain.goals.validator;

import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import java.time.LocalDateTime;

public class SubGoalValidator {

    public static void validateSubGoalCreation(SubGoalCreateRequestDTO subGoalCreateRequestDTO) {
        if (subGoalCreateRequestDTO.getSubGoals().isEmpty()) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_CREATE_EMPTY);
        }

        for (SubGoalRequestDTO requestDTO : subGoalCreateRequestDTO.getSubGoals()) {
            if (requestDTO.getTitle().isBlank()) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_EMPTY);
            }
            if (requestDTO.getTitle().length() > ValidationConstants.TITLE_MAX) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
            }

            if (requestDTO.getIsTimeSelected() == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_IS_TIME_SELECTED_IS_NULL);
            }

            if (requestDTO.getIsTimeSelected() && requestDTO.getStartDateTime() == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_START_DATETIME_IS_NULL);
            }

            if (requestDTO.getIsTimeSelected() && requestDTO.getEndDateTime() == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_END_DATETIME_IS_NULL);
            }

            if (!requestDTO.getIsTimeSelected() && requestDTO.getStartDateTime() != null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_START_DATETIME_INVALID);
            }

            if (!requestDTO.getIsTimeSelected() && requestDTO.getEndDateTime() != null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_END_DATETIME_INVALID);
            }

            if (requestDTO.getIsTimeSelected()) {
                LocalDateTime startDateTime = requestDTO.getStartDateTime();
                LocalDateTime endDataTime = requestDTO.getEndDateTime();

                if (startDateTime.isAfter(endDataTime)) {
                    throw new SubGoalException(
                        SubGoalExceptionType.SUB_GOAL_DATETIME_RANGE_INVALID);
                }
            }
        }
    }

    public static void validateUpdateRequestDTO(SubGoalUpdateRequestDTO requestDTO) {
        if (requestDTO.getTitle().length() > ValidationConstants.SUB_TITLE_MAX) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
        }

        if (requestDTO.getIsTimeSelected() == null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_IS_TIME_SELECTED_IS_NULL);
        }

        if (requestDTO.getIsTimeSelected() && requestDTO.getStartDateTime() == null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_START_DATETIME_IS_NULL);
        }

        if (requestDTO.getIsTimeSelected() && requestDTO.getEndDateTime() == null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_END_DATETIME_IS_NULL);
        }

        if (!requestDTO.getIsTimeSelected() && requestDTO.getStartDateTime() != null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_START_DATETIME_INVALID);
        }

        if (!requestDTO.getIsTimeSelected() && requestDTO.getEndDateTime() != null) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_END_DATETIME_INVALID);
        }

        if (requestDTO.getIsTimeSelected()) {
            LocalDateTime startDateTime = requestDTO.getStartDateTime();
            LocalDateTime endDataTime = requestDTO.getEndDateTime();

            if (startDateTime.isAfter(endDataTime)) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_DATETIME_RANGE_INVALID);
            }
        }
    }
}
