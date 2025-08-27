package com.hotpack.krocs.domain.plans.service;


import com.hotpack.krocs.domain.plans.converter.SubPlanConverter;
import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanUpdateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanCreateResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanListResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanResponseDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanUpdateResponseDTO;
import com.hotpack.krocs.domain.plans.exception.SubPlanException;
import com.hotpack.krocs.domain.plans.exception.SubPlanExceptionType;
import com.hotpack.krocs.domain.plans.facade.PlanRepositoryFacade;
import com.hotpack.krocs.domain.plans.facade.SubPlanRepositoryFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubPlanServiceImpl implements SubPlanService {

    private final SubPlanConverter subPlanConverter;
    private final SubPlanRepositoryFacade subPlanRepositoryFacade;
    private final PlanRepositoryFacade planRepositoryFacade;


    @Override
    @Transactional
    public SubPlanCreateResponseDTO createSubPlans(Long planId,
        SubPlanCreateRequestDTO requestDTO) {
        try {
            if (planId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_PLAN_ID_IS_NULL);
            }
            validateSubPlanCreation(requestDTO);

            Plan plan = planRepositoryFacade.findActivePlanById(planId);

            List<SubPlan> subPlans = subPlanConverter.toSubPlanEntityList(plan, requestDTO);
            List<SubPlan> createdSubPlans = subPlanRepositoryFacade.saveSubPlans(subPlans);
            List<SubPlanResponseDTO> subPlanResponseDTOs = subPlanConverter.toSubPlanResponseListDTO(
                createdSubPlans);

            return SubPlanCreateResponseDTO.builder()
                .planId(planId)
                .createdSubPlans(subPlanResponseDTOs)
                .build();

        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("소계획 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
        }
    }

    private void validateSubPlanCreation(SubPlanCreateRequestDTO subPlanCreateRequestDTO) {
        if (subPlanCreateRequestDTO.getSubPlans().isEmpty()) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_CREATE_EMPTY);
        }

        for (SubPlanRequestDTO subPlanRequestDTO : subPlanCreateRequestDTO.getSubPlans()) {
            if (subPlanRequestDTO.getTitle().isBlank()) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_TITLE_EMPTY);
            }
            if (subPlanRequestDTO.getTitle().length() > 200) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_TITLE_TOO_LONG);
            }
        }
    }

    @Override
    public SubPlanListResponseDTO getAllSubPlans(Long planId) {
        try {
            if (planId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_PLAN_ID_IS_NULL);
            }

            Plan plan = planRepositoryFacade.findActivePlanById(planId);

            List<SubPlan> subPlans = subPlanRepositoryFacade.findActiveSubPlansByPlan(plan);

            // 빈 리스트는 정상 응답으로 간주하고 그대로 반환
            List<SubPlanResponseDTO> subPlanResponseDTOs = subPlanConverter.toSubPlanResponseListDTO(
                subPlans);

            return SubPlanListResponseDTO.builder()
                .subPlans(subPlanResponseDTOs)
                .build();
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("소계획 전체 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_READ_FAILED);
        }
    }

    @Override
    public SubPlanResponseDTO getSubPlan(Long planId, Long subPlanId) {
        try {
            if (planId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_PLAN_ID_IS_NULL);
            }
            if (subPlanId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_ID_IS_NULL);
            }

            Plan plan = planRepositoryFacade.findActivePlanById(planId);
            List<SubPlan> subPlans = subPlanRepositoryFacade.findActiveSubPlansByPlan(plan);
            SubPlan subPlan = subPlanRepositoryFacade.findActiveSubPlanBySubPlanId(subPlanId);

            if (!subPlans.contains(subPlan)) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_NOT_BELONG_TO_PLAN);
            }

            return subPlanConverter.toSubPlanResponseDTO(subPlan);

        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("소계획 단건 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_READ_FAILED);
        }
    }

    @Override
    @Transactional
    public SubPlanUpdateResponseDTO updateSubPlan(Long subPlanId,
        SubPlanUpdateRequestDTO requestDTO) {
        try {
            if (subPlanId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_ID_IS_NULL);
            }

            validateBusinessRules(requestDTO);

            SubPlan subPlan = subPlanRepositoryFacade.findActiveSubPlanBySubPlanId(subPlanId);

            boolean wasCompleted = Boolean.TRUE.equals(subPlan.getIsCompleted());
            subPlan.updateFrom(requestDTO, wasCompleted);

            return subPlanConverter.toSubPlanUpdateResponseDTO(subPlan);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("소계획 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_UPDATE_FAILED);
        }
    }

    private void validateBusinessRules(SubPlanUpdateRequestDTO requestDTO) {
        if (requestDTO.getTitle() == null) {
            return;
        }

        if (requestDTO.getTitle().length() > 200) {
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_TITLE_TOO_LONG);
        }
    }


    @Override
    @Transactional
    public void deleteSubPlan(Long subPlanId) {
        try {
            if (subPlanId == null) {
                throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_ID_IS_NULL);
            }
            subPlanRepositoryFacade.deleteActiveSubPlanBySubPlanId(subPlanId);
        } catch (SubPlanException e) {
            throw e;
        } catch (Exception e) {
            log.error("소계획 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubPlanException(SubPlanExceptionType.SUB_PLAN_DELETE_FAILED);
        }
    }
}

