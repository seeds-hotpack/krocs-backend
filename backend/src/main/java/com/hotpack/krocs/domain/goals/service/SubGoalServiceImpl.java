package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.validator.SubGoalValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubGoalServiceImpl implements SubGoalService {

    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final SubGoalConverter subGoalConverter;
    private final UserRepositoryFacade userRepositoryFacade;
    private final GoalRepositoryFacade goalRepositoryFacade;

    @Override
    @Transactional
    public SubGoalCreateResponseDTO createSubGoals(Long userId, Long goalId,
        SubGoalCreateRequestDTO requestDTO) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }
            SubGoalValidator.validateSubGoalCreation(requestDTO);

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal goal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            if (goal == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_NOT_FOUND);
            }

            List<SubGoal> subGoals = subGoalConverter.toSubGoalEntityList(goal, requestDTO);
            List<SubGoal> createdSubGoals = subGoalRepositoryFacade.saveSubGoals(subGoals);
            List<SubGoalResponseDTO> subGoalResponseDTOs = subGoalConverter.toSubGoalResponseListDTO(
                createdSubGoals);

            return SubGoalCreateResponseDTO
                .builder()
                .goalId(goalId)
                .createdSubGoals(subGoalResponseDTOs)
                .build();

        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_CREATE_FAILED);
        }
    }

    @Override
    @Transactional
    public SubGoalUpdateResponseDTO updateSubGoal(Long userId, Long goalId, Long subGoalId,
        SubGoalUpdateRequestDTO requestDTO) {
        try {
            if (subGoalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
            }

            validateSubGoalAccess(userId, goalId, subGoalId);
            SubGoalValidator.validateUpdateRequestDTO(requestDTO);

            SubGoal subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId);
            subGoal.updateFrom(requestDTO);

            return SubGoalConverter.toSubGoalUpdateResponseDTO(
                subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId));
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
        }
    }

    @Override
    public SubGoalListResponseDTO getAllSubGoals(Long userId, Long goalId) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal goal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);

            List<SubGoal> subGoals = subGoalRepositoryFacade.findActiveSubGoalsByGoal(goal);
            List<SubGoalResponseDTO> subGoalResponseDTOS = subGoalConverter.toSubGoalResponseListDTO(
                subGoals);

            return SubGoalListResponseDTO
                .builder()
                .subGoals(subGoalResponseDTOS)
                .build();
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 전체 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_READ_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteSubGoal(Long userId, Long goalId, Long subGoalId) {
        try {
            if (subGoalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
            }

            validateSubGoalAccess(userId, goalId, subGoalId);

            subGoalRepositoryFacade.deleteActiveSubGoalBySubGoalId(subGoalId);
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
        }
    }

    @Override
    public List<SubGoalResponseDTO> getSubGoalsInDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            List<Goal> goals = goalRepositoryFacade.findAllActiveGoalsByUser(user);

            List<SubGoal> allSubGoals = new ArrayList<>();
            for (Goal goal : goals) {
                List<SubGoal> subGoals = subGoalRepositoryFacade.findActiveSubGoalsByGoal(goal);

                List<SubGoal> filteredSubGoals = subGoals.stream()
                        .filter(subGoal -> {
                            if (subGoal.getStartDateTime() == null || subGoal.getEndDateTime() == null) {
                                return false;
                            }
                            LocalDateTime subGoalStart = subGoal.getStartDateTime();
                            LocalDateTime subGoalEnd = subGoal.getEndDateTime();

                            return (subGoalStart.isBefore(endDateTime) || subGoalStart.isEqual(endDateTime))
                                    && (subGoalEnd.isAfter(startDateTime) || subGoalEnd.isEqual(startDateTime));
                        })
                        .collect(Collectors.toList());

                allSubGoals.addAll(filteredSubGoals);
            }

            return subGoalConverter.toSubGoalResponseListDTO(allSubGoals);

        } catch (GoalException | SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("날짜 범위 소목표 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_READ_FAILED);
        }
    }

    private void validateSubGoalAccess(Long userId, Long goalId, Long subGoalId) {
        boolean accessible = subGoalRepositoryFacade.existsValidSubGoal(
            userId, goalId, subGoalId);

        if (!accessible) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ACCESS_DENIED);
        }
    }

}
