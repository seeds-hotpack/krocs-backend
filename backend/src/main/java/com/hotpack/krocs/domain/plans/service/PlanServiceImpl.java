package com.hotpack.krocs.domain.plans.service;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.plans.converter.PlanConverter;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.facade.PlanRepositoryFacade;
import com.hotpack.krocs.domain.plans.validator.PlanValidator;
import com.hotpack.krocs.domain.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

    private final PlanRepositoryFacade planRepositoryFacade;
    private final PlanConverter planConverter;
    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final PlanValidator planValidator;

    @Override
    @Transactional
    public PlanResponseDTO createPlan(PlanCreateRequestDTO requestDTO, Long userId,
        Long subGoalId) {
        try {
            planValidator.validatePlanCreation(requestDTO, subGoalId);

            SubGoal subGoal = null;
            Goal goal = null;

            if (subGoalId != null) {
                subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId);
                if (subGoal == null) {
                    throw new PlanException(PlanExceptionType.PLAN_SUB_GOAL_NOT_FOUND);
                }

                goal = subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId);
                if (goal == null) {
                    throw new PlanException(PlanExceptionType.PLAN_GOAL_NOT_FOUND);
                }
            }

            Plan plan;
            if (userId != null) {
                User userRef = User.builder()
                    .userId(userId)
                    .build();
                plan = planConverter.toEntity(requestDTO, goal, subGoal, userRef);
            } else {
                plan = planConverter.toEntity(requestDTO, goal, subGoal);
            }
            Plan savedPlan = planRepositoryFacade.savePlan(plan);

            return planConverter.toEntity(savedPlan);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("일정 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_CREATION_FAILED);
        }
    }

    @Override
    public PlanListResponseDTO getPlans(LocalDate date, Long userId) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Plan> plans = planRepositoryFacade.findActivePlansByDateRange(startOfDay, endOfDay,
                userId);
            List<PlanResponseDTO> planResponseDTOs = planConverter.toListPlanResponseDTO(plans);

            return PlanListResponseDTO.builder()
                .plans(planResponseDTOs)
                .build();
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("일정 전체 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }

    @Override
    public PlanResponseDTO getPlanById(Long planId, Long userId) {
        try {
            planValidator.validateGetPlan(planId);
            Plan plan = planRepositoryFacade.findActivePlanById(planId);
            if (plan == null) {
                throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
            }
            return planConverter.toEntity(plan);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("특정 일정 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }

    @Override
    @Transactional
    public PlanResponseDTO updatePlanById(Long planId, Long subGoalId, PlanUpdateRequestDTO request,
        Long userId) {
        try {
            planValidator.validateUpdatePlan(planId);

            Plan plan = planRepositoryFacade.findActivePlanById(planId);
            if (plan == null) {
                throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
            }

            SubGoal subGoal = plan.getSubGoal();
            Goal goal = plan.getGoal();
            if (subGoalId != null) {
                subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId);
                if (subGoal == null) {
                    throw new PlanException(PlanExceptionType.PLAN_SUB_GOAL_NOT_FOUND);
                }
                goal = subGoalRepositoryFacade.findActiveGoalBySubGoalId(subGoalId);
            }

            if (request.getTitle() != null) {
                planValidator.validateTitle(request.getTitle());
            }

            Boolean allDay = plan.getAllDay();
            LocalDateTime startDateTime = plan.getStartDateTime();
            LocalDateTime endDateTime = plan.getEndDateTime();
            if (request.getAllDay() != null) {
                allDay = request.getAllDay();
            }
            if (request.getStartDateTime() != null) {
                startDateTime = request.getStartDateTime();
            }
            if (request.getEndDateTime() != null) {
                endDateTime = request.getEndDateTime();
            }

            if (request.getStartDateTime() != null || request.getEndDateTime() != null) {
                planValidator.validateDateRange(startDateTime, endDateTime);
            }

            if (Boolean.TRUE.equals(allDay)) {
                if (startDateTime != null) {
                    startDateTime = startDateTime.toLocalDate().atStartOfDay();
                }
                if (endDateTime != null) {
                    endDateTime = endDateTime.toLocalDate().atTime(23, 59, 59);
                }
            }

            if (request.getAllDay() != null || request.getStartDateTime() != null
                || request.getEndDateTime() != null) {
                planValidator.validateAllDayDateTime(allDay, startDateTime, endDateTime);
            }

            PlanUpdateRequestDTO updatePlanRequestDTO = planConverter.toUpdatePlanRequestDTO(
                request, allDay, startDateTime, endDateTime);

            plan.updateFrom(updatePlanRequestDTO, goal, subGoal);

            return planConverter.toEntity(plan);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("일정 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deletePlan(Long planId, Long userId) {
        try {
            planValidator.validateDeletePlan(planId);
            if (planRepositoryFacade.findActivePlanById(planId) == null) {
                throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
            }

            planRepositoryFacade.deleteActivePlanByPlanId(planId);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("계획 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_DELETE_FAILED);
        }
    }
}
