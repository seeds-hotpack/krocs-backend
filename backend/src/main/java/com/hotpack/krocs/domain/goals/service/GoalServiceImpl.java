package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.converter.GoalConverter;
import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import com.hotpack.krocs.domain.goals.exception.GoalException;
import com.hotpack.krocs.domain.goals.exception.GoalExceptionType;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalServiceImpl implements GoalService {

    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final UserRepositoryFacade userRepositoryFacade;
    private final SubGoalConverter subGoalConverter;
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
    public List<GoalResponseDTO> getGoalByUser(Long userId, LocalDate date) {
        try {
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new GoalException(GoalExceptionType.GOAL_USER_NOT_FOUND);
            }

            List<Goal> goals;
            if (date != null) {
                goals = goalRepositoryFacade.findActiveGoalByUserAndDate(user, date);
            } else {
                goals = goalRepositoryFacade.findAllActiveGoalsByUser(user);
            }
            return goalConverter.toGoalResponseDTO(goals);
        } catch (GoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("대목표 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
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

            // validator 추출
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

    @Override
    @Transactional
    public SubGoalCreateResponseDTO createSubGoals(Long userId, Long goalId,
        SubGoalCreateRequestDTO requestDTO) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }
            validateSubGoalCreation(requestDTO);

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

    private void validateSubGoalCreation(SubGoalCreateRequestDTO subGoalCreateRequestDTO) {
        if (subGoalCreateRequestDTO.getSubGoals().isEmpty()) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_CREATE_EMPTY);
        }

        for (SubGoalRequestDTO subGoalRequestDTO : subGoalCreateRequestDTO.getSubGoals()) {
            if (subGoalRequestDTO.getTitle().isBlank()) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_EMPTY);
            }
            if (subGoalRequestDTO.getTitle().length() > ValidationConstants.TITLE_MAX) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
            }
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
}