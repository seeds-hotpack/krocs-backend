package com.hotpack.krocs.domain.plans.service;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
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
    private final PlanConverter planConverter;
    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final PlanValidator planValidator;

    @Override
    @Transactional
    public PlanResponseDTO createPlan(PlanCreateRequestDTO requestDTO, Long userId) {
        try {
            planValidator.validatePlanCreation(requestDTO);

            Plan plan;
            if (userId != null) {
                User userRef = User.builder()
                    .userId(userId)
                    .build();
                plan = planConverter.toEntity(requestDTO, userRef);
            } else {
                plan = planConverter.toEntity(requestDTO);
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
    public PlanResponseDTO updatePlanById(Long planId, PlanUpdateRequestDTO request,
        Long userId) {
        try {
            planValidator.validateUpdatePlan(planId);

            Plan plan = planRepositoryFacade.findActivePlanById(planId);
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

    @Override
    public MonthlyPlanResponseDTO getMonthlyPlans(int year, int month, Long userId) {
        try {
            planValidator.validateMonthlyPlanRequest(year, month);

            List<Plan> monthlyPlans = planRepositoryFacade.findActivePlansByMonth(year, month, userId);

            // 날짜별로 그룹핑
            Map<LocalDate, List<Plan>> plansByDate = monthlyPlans.stream()
                    .collect(Collectors.groupingBy(plan -> plan.getStartDateTime().toLocalDate()));

            // 해당 월의 모든 날짜 생성 (1일부터 마지막일까지)
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            List<DailyPlanSummaryDTO> dailyPlans = new ArrayList<>();

            // 각 날짜별로 DailyPlanSummaryDTO 생성
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                List<Plan> plansForDate = plansByDate.getOrDefault(date, Collections.emptyList());
                List<PlanResponseDTO> planResponseDTOs = planConverter.toListPlanResponseDTO(plansForDate);

                DailyPlanSummaryDTO dailySummary = DailyPlanSummaryDTO.builder()
                        .date(date)
                        .planCount(plansForDate.size())
                        .plans(planResponseDTOs)
                        .build();

                dailyPlans.add(dailySummary);
            }

            return MonthlyPlanResponseDTO.builder()
                    .year(year)
                    .month(month)
                    .dailyPlans(dailyPlans)
                    .build();
        } catch (PlanException e) {
                throw e;
        } catch (Exception e) {
            log.error("월별 일정 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new PlanException(PlanExceptionType.PLAN_FOUND_FAILED);
        }
    }
}
