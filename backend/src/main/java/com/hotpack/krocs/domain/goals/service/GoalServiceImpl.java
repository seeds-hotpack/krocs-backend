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
    private final SubGoalConverter subGoalConverter;
    private final GoalRepositoryFacade goalRepositoryFacade;
    private final GoalConverter goalConverter;
    private final GoalValidator goalValidator;

    @Override
    @Transactional
    public GoalCreateResponseDTO createGoal(GoalCreateRequestDTO requestDTO, Long userId) {
        try {
            goalValidator.validateGoalCreation(requestDTO);

            if (goalRepositoryFacade.existsActiveGoalByTitle(requestDTO.getTitle())) {
                throw new GoalException(GoalExceptionType.GOAL_DUPLICATE_TITLE);
            }
            Goal goal;
            if (userId != null) {
                User userRef = User.builder()
                    .userId(userId)
                    .build();
                goal = goalConverter.toEntity(requestDTO, userRef);
            } else {
                goal = goalConverter.toEntity(requestDTO);
            }
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
            List<Goal> goals;
            if (date != null) {
                goals = goalRepositoryFacade.findActiveGoalByDate(date);
            } else {
                goals = goalRepositoryFacade.findAllActiveGoals();
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

            Goal existingGoal = goalRepositoryFacade.findActiveGoalById(goalId);
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

            Goal existingGoal = goalRepositoryFacade.findActiveGoalById(goalId);
            if (existingGoal == null) {
                throw new GoalException(GoalExceptionType.GOAL_NOT_FOUND);
            }

            if (requestDTO.getTitle() != null) {
                goalValidator.validateTitle(requestDTO.getTitle());
            }
            if (goalRepositoryFacade.existsActiveGoalByTitleAndGoalIdNot(requestDTO.getTitle(),
                goalId)) {
                throw new GoalException(GoalExceptionType.GOAL_DUPLICATE_TITLE);
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
            Goal updatedGoal = goalRepositoryFacade.findActiveGoalById(goalId);

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

            Goal goal = goalRepositoryFacade.findActiveGoalById(goalId);
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
    public SubGoalCreateResponseDTO createSubGoals(Long goalId,
        SubGoalCreateRequestDTO requestDTO) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }
            validateSubGoalCreation(requestDTO);

            Goal goal = goalRepositoryFacade.findActiveGoalById(goalId);

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
            if (subGoalRequestDTO.getTitle().length() > 200) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
            }
        }
    }

    // TODO: 수정중
    @Override
    public SubGoalListResponseDTO getAllSubGoals(Long goalId) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }

            Goal goal = goalRepositoryFacade.findActiveGoalById(goalId);

            List<SubGoal> subGoals = subGoalRepositoryFacade.findSubGoalsByGoal(goal);
            if (subGoals.isEmpty()) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_FOUND);
            }
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
    public SubGoalResponseDTO getSubGoal(Long goalId, Long subGoalId) {
        try {
            if (goalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_GOAL_ID_IS_NULL);
            }
            if (subGoalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
            }

            Goal goal = goalRepositoryFacade.findActiveGoalById(goalId);
            List<SubGoal> subGoals = subGoalRepositoryFacade.findSubGoalsByGoal(goal);
            SubGoal subGoal = subGoalRepositoryFacade.findSubGoalBySubGoalId(subGoalId);
            if (!subGoals.contains(subGoal)) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_NOT_BELONG_TO_GOAL);
            }
            return subGoalConverter.toSubGoalResponseDTO(subGoal);

        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 단건 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_READ_FAILED);
        }
    }
}