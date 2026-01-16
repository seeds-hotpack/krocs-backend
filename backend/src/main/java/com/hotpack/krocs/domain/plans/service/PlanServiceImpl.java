package com.hotpack.krocs.domain.plans.service;

import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import com.hotpack.krocs.domain.plans.converter.PlanConverter;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.dto.request.PlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.PlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.DailyPlanSummaryDTO;
import com.hotpack.krocs.domain.plans.dto.response.MonthlyPlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.PlanResponseDTO;
import com.hotpack.krocs.domain.plans.exception.PlanException;
import com.hotpack.krocs.domain.plans.exception.PlanExceptionType;
import com.hotpack.krocs.domain.plans.facade.PlanRepositoryFacade;
import com.hotpack.krocs.domain.plans.validator.PlanValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final UserRepositoryFacade userRepositoryFacade;
    private final PlanConverter planConverter;
    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final PlanValidator planValidator;

    @Override
    @Transactional
    public PlanResponseDTO createPlan(PlanCreateRequestDTO requestDTO, Long userId) {
        try {
            planValidator.validatePlanCreation(requestDTO);
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new PlanException(PlanExceptionType.PLAN_USER_NOT_FOUND);
            }

            Plan plan = planConverter.toEntity(requestDTO, user);
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

            List<Plan> plans = planRepositoryFacade.findActivePlansByDate(date, userId);
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
            Plan plan = planRepositoryFacade.findActivePlanByPlanIdAndUserId(planId, userId);
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
    public PlanResponseDTO updatePlanById(Long planId, PlanUpdateRequestDTO request,
        Long userId) {
        try {
            planValidator.validateUpdatePlan(planId);

            Plan plan = planRepositoryFacade.findActivePlanByPlanIdAndUserId(planId, userId);
            if (plan == null) {
                throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
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

            plan.updateFrom(updatePlanRequestDTO);

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
            if (planRepositoryFacade.findActivePlanByPlanIdAndUserId(planId, userId) == null) {
                throw new PlanException(PlanExceptionType.PLAN_NOT_FOUND);
            }

            planRepositoryFacade.deleteActivePlanByPlanId(planId, userId);
        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("계획 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_DELETE_FAILED);
        }
    }

    @Override
    public MonthlyPlanResponseDTO getMonthlyPlans(int year, int month, Long userId) {
        try {
            planValidator.validateMonthlyPlanRequest(year, month);

            List<Plan> monthlyPlans = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

            Map<LocalDate, List<Plan>> plansByDate = monthlyPlans.stream()
                    .collect(Collectors.groupingBy(plan -> plan.getStartDateTime().toLocalDate()));

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            List<DailyPlanSummaryDTO> dailyPlans = new ArrayList<>();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                List<Plan> plansForDate = plansByDate.getOrDefault(date, Collections.emptyList());
                List<PlanResponseDTO> planResponseDTOs = planConverter.toListPlanResponseDTO(plansForDate);

                DailyPlanSummaryDTO dailySummary = planConverter.toDailyPlanSummaryDTO(date, plansForDate, planResponseDTOs);

                dailyPlans.add(dailySummary);
            }

            return planConverter.toMonthlyPlanResponseDTO(year, month, dailyPlans);
        } catch (PlanException e) {
                throw e;
        } catch (Exception e) {
            log.error("월별 일정 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }

    @Override
    public List<PlanResponseDTO> getPlansInDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new PlanException(PlanExceptionType.PLAN_USER_NOT_FOUND);
            }

            List<Plan> filteredPlans = planRepositoryFacade.findActivePlansByDateRange(
                    startDateTime,
                    endDateTime,
                    userId
            );

            return planConverter.toListPlanResponseDTO(filteredPlans);

        } catch (PlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("날짜 범위 플랜 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }
}
