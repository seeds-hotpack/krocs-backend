package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.converter.GoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalSearchRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.util.SortUtils;
import java.time.LocalDate;
import java.util.Comparator;
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
public class GoalServiceImpl implements GoalService {

    private final UserRepositoryFacade userRepositoryFacade;
    private final GoalRepositoryFacade goalRepositoryFacade;
    private final GoalConverter goalConverter;
    private final GoalValidator goalValidator;

    @Override
    @Transactional
    public GoalCreateResponseDTO createGoal(GoalCreateRequestDTO requestDTO, Long userId) {
        try {
            goalValidator.validateGoalCreation(requestDTO);
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal goal = goalConverter.toEntity(requestDTO, user);
            Goal savedGoal = goalRepositoryFacade.saveGoal(goal);

            return goalConverter.toCreateResponseDTO(savedGoal);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GoalException(GoalExceptionType.GOAL_CREATION_FAILED);
        }
    }

    @Override
    public List<GoalResponseDTO> getGoalsByUser(Long userId, LocalDate searchDate, String keyword, String status) {
        try {
            GoalSearchRequestDTO searchRequest = goalConverter.toGoalSearchRequestDTO(searchDate, keyword, status);

            List<Goal> goals = goalRepositoryFacade.findGoalsWithFilters(
                    userId,
                    searchRequest.getKeyword(),
                    searchRequest.getSearchDate()
            );

            if (searchRequest.getStatus() != null && !searchRequest.getStatus().isEmpty()) {
                LocalDate now = LocalDate.now();
                goals = goals.stream().filter(goal -> {
                    switch (searchRequest.getStatus().toUpperCase()) {
                        case "COMPLETED":
                            return goal.getIsCompleted();
                        case "IN_PROGRESS":
                            return !goal.getIsCompleted() &&
                                    (goal.getEndDate() == null || !goal.getEndDate().isBefore(now));
                        case "EXPIRED":
                            return !goal.getIsCompleted() &&
                                    goal.getEndDate() != null && goal.getEndDate().isBefore(now);
                        default:
                            return true;
                    }
                }).collect(Collectors.toList());
            }

            List<GoalResponseDTO> goalResponseDTOs = goalConverter.toGoalResponseDTO(goals);

            goalResponseDTOs.sort(Comparator
                    .comparing(GoalResponseDTO::getEndDate,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(goal -> SortUtils.sortByKoreanFirst(goal.getTitle()))
                    .thenComparing(GoalResponseDTO::getGoalId));

            return goalResponseDTOs;
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 전체 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Override
    public GoalResponseDTO getGoalByGoalId(Long userId, Long goalId) {
        try {
            goalValidator.validateGoalIdParameter(goalId);
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal existingGoal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            if (existingGoal == null) {
                throw new GoalException(GoalExceptionType.GOAL_NOT_FOUND);
            }

            return goalConverter.toGoalResponseDTO(existingGoal);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GoalException(GoalExceptionType.GOAL_FOUND_FAILED);
        }
    }

    @Override
    @Transactional
    public GoalResponseDTO updateGoalById(Long goalId, GoalUpdateRequestDTO requestDTO,
        Long userId) {
        try {
            goalValidator.validateGoalIdParameter(goalId);

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal existingGoal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            if (existingGoal == null) {
                throw new GoalException(GoalExceptionType.GOAL_NOT_FOUND);
            }

            if (requestDTO.getTitle() != null) {
                goalValidator.validateTitle(requestDTO.getTitle());
            }
            if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
                goalValidator.validateDateRange(requestDTO.getStartDate(), requestDTO.getEndDate());
            } else if (requestDTO.getStartDate() != null) {
                goalValidator.validateDateRange(requestDTO.getStartDate(),
                    existingGoal.getEndDate());
            } else if (requestDTO.getEndDate() != null) {
                goalValidator.validateDateRange(existingGoal.getStartDate(),
                    requestDTO.getEndDate());
            }

            existingGoal.updateFrom(requestDTO);
            Goal updatedGoal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);

            return goalConverter.toGoalResponseDTO(updatedGoal);

        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GoalException(GoalExceptionType.GOAL_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        try {
            goalValidator.validateGoalIdParameter(goalId);
            if (!goalRepositoryFacade.existsActiveGoalById(goalId)) {
                throw new GoalException(GoalExceptionType.GOAL_NOT_FOUND);
            }

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            Goal goal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            goal.delete();

        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GoalException(GoalExceptionType.GOAL_DELETE_FAILED);
        }
    }
}