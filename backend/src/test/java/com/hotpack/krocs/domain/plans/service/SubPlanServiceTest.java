package com.hotpack.krocs.domain.plans.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class SubPlanServiceTest {

    @Mock
    private PlanRepositoryFacade planRepositoryFacade;
    @Mock
    private SubPlanRepositoryFacade subPlanRepositoryFacade;
    @Mock
    private SubPlanConverter subPlanConverter;
    @InjectMocks
    private SubPlanServiceImpl subPlanService;

    private Plan validPlan;
    private SubPlan validSubPlan;
    private SubPlanResponseDTO validSubPlanResponseDTO;
    private SubPlanRequestDTO validSubPlanRequestDTO;
    private SubPlanCreateRequestDTO validSubPlanCreateRequestDTO;
    private SubPlanUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        validPlan = Plan.builder()
            .planId(1L)
            .build();

        validSubPlan = SubPlan.builder()
            .subPlanId(1L)
            .title("테스트 소계획1")
            .isCompleted(false)
            .completedAt(null)
            .build();

        validSubPlanResponseDTO = SubPlanResponseDTO.builder()
            .subPlanId(1L)
            .title("테스트 소계획1")
            .isCompleted(false)
            .build();

        validSubPlanRequestDTO = SubPlanRequestDTO.builder()
            .title("테스트 소계획1")
            .build();

        validSubPlanCreateRequestDTO = SubPlanCreateRequestDTO.builder()
            .subPlans(List.of(validSubPlanRequestDTO))
            .build();

        updateRequestDTO = SubPlanUpdateRequestDTO.builder()
            .title("수정된 소계획 제목")
            .isCompleted(true)
            .build();
    }

    @Test
    @DisplayName("소계획 생성 성공 테스트")
    void createSubPlans_Success() {
        // given
        LocalDateTime now = LocalDateTime.now();

        SubPlanResponseDTO validSubPlanResponseDTO = SubPlanResponseDTO.builder()
            .subPlanId(1L)
            .title("테스트 소계획1")
            .isCompleted(false)
            .createdAt(now.minusDays(2))
            .updatedAt(now)
            .completedAt(null)
            .build();

        when(planRepositoryFacade.findActivePlanById(1L)).thenReturn(validPlan);
        when(subPlanRepositoryFacade.saveSubPlans(List.of(validSubPlan))).thenReturn(
            List.of(validSubPlan));
        when(subPlanConverter.toSubPlanEntityList(any(), any())).thenReturn(List.of(validSubPlan));
        when(subPlanConverter.toSubPlanResponseListDTO(any())).thenReturn(
            List.of(validSubPlanResponseDTO));

        // when
        SubPlanCreateResponseDTO result = subPlanService.createSubPlans(1L,
            validSubPlanCreateRequestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanId()).isEqualTo(1L);
        assertThat(result.getCreatedSubPlans()).isNotEmpty();

        SubPlanResponseDTO dto = result.getCreatedSubPlans().get(0);
        assertThat(dto.getSubPlanId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("테스트 소계획1");
        assertThat(dto.getIsCompleted()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(now.minusDays(2));
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
        assertThat(dto.getCompletedAt()).isNull(); // 미완료 상태
    }

    @Test
    @DisplayName("소계획 생성 - PlanRepository에서 예외 발생")
    void createSubPlan_PlanRepositoryException() {
        // given
        when(planRepositoryFacade.findActivePlanById(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, validSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
    }

    @Test
    @DisplayName("소계획 생성 - Plan 조회 실패")
    void createSubPlan_PlanRepositoryNotFound() {
        // given
        when(planRepositoryFacade.findActivePlanById(any())).thenThrow(
            new SubPlanException(SubPlanExceptionType.SUB_PLAN_PLAN_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, validSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_PLAN_NOT_FOUND);
    }

    @Test
    @DisplayName("소계획 생성 - SubPlanCreateRequest.subPlans()가 비어있는 리스트인 경우")
    void createSubPlans_subPlansIsEmpty() {
        // given
        SubPlanCreateRequestDTO invalidSubPlanCreateRequestDTO = SubPlanCreateRequestDTO.builder()
            .subPlans(new ArrayList<>()).build();

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, invalidSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_CREATE_EMPTY);
    }

    @Test
    @DisplayName("소계획 생성 - SubPlanRequestDTO.title()이 Blank인 경우")
    void createSubPlans_titleIsBlank() {
        // given
        SubPlanRequestDTO invalidSubPlanRequestDTO = SubPlanRequestDTO.builder().title("").build();
        SubPlanCreateRequestDTO invalidSubPlanCreateRequestDTO = SubPlanCreateRequestDTO.builder()
            .subPlans(List.of(invalidSubPlanRequestDTO)).build();

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, invalidSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_TITLE_EMPTY);
    }

    @Test
    @DisplayName("소계획 생성 - SubPlanRequestDTO.title()이 200자를 초과하는 경우")
    void createSubPlans_titleExceedsMaxLength() {
        // given
        SubPlanRequestDTO invalidSubPlanRequestDTO = SubPlanRequestDTO.builder()
            .title("1".repeat(210)) // 210자
            .build();
        SubPlanCreateRequestDTO invalidSubPlanCreateRequestDTO = SubPlanCreateRequestDTO.builder()
            .subPlans(List.of(invalidSubPlanRequestDTO)).build();

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, invalidSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("소계획 생성 - SubPlanRepository 저장 실패")
    void createSubPlan_SubPlansRepositoryException() {
        // given
        when(subPlanRepositoryFacade.saveSubPlans(any())).thenThrow(
            new RuntimeException("데이터베이스 오류"));
        when(planRepositoryFacade.findActivePlanById(1L)).thenReturn(validPlan);

        // when & then
        assertThatThrownBy(() -> subPlanService.createSubPlans(1L, validSubPlanCreateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_CREATE_FAILED);
    }

    @Test
    @DisplayName("소계획 전체 조회 - planId가 null인 경우")
    void getAllSubPlans_planIdIsNull() {
        // when & then
        assertThatThrownBy(() -> subPlanService.getAllSubPlans(null))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_PLAN_ID_IS_NULL);
    }

    @Test
    @DisplayName("소계획 전체 조회 - SubPlanRepository에서 조회 중 예상치 못한 오류가 발생하는 경우")
    void getAllSubPlans_SubPlanRepositoryException() {
        // given
        when(planRepositoryFacade.findActivePlanById(1L)).thenReturn(validPlan);
        when(subPlanRepositoryFacade.findSubPlansByPlan(any())).thenThrow(new RuntimeException());

        // when & then
        assertThatThrownBy(() -> subPlanService.getAllSubPlans(1L))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_READ_FAILED);
    }

    @Test
    @DisplayName("소계획 전체 조회 - 조회된 SubPlan이 한 건도 없는 경우")
    void getAllSubPlans_EmptySubPlanList() {
        // given
        when(planRepositoryFacade.findActivePlanById(1L)).thenReturn(validPlan);
        when(subPlanRepositoryFacade.findSubPlansByPlan(any())).thenReturn(new ArrayList<>());
        when(subPlanConverter.toSubPlanResponseListDTO(anyList())).thenReturn(new ArrayList<>());

        // when
        SubPlanListResponseDTO response = subPlanService.getAllSubPlans(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSubPlans()).isEmpty();
    }

    @Test
    @DisplayName("소계획 단건 조회 - planId가 null인 경우")
    void getSubPlan_planIdIsNull() {
        // when & then
        assertThatThrownBy(() -> subPlanService.getSubPlan(null, 1L))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_PLAN_ID_IS_NULL);
    }

    @Test
    @DisplayName("소계획 단건 조회 - subPlanId가 null인 경우")
    void getSubPlan_subPlanIdIsNull() {
        // when & then
        assertThatThrownBy(() -> subPlanService.getSubPlan(1L, null))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_ID_IS_NULL);
    }

    @Test
    @DisplayName("소계획 단건 조회 - 해당 소계획이 주계획에 포함되지 않은 경우")
    void getSubPlan_subPlanNotBelongToPlan() {
        // given
        SubPlan otherSubPlan = SubPlan.builder().subPlanId(2L).title("다른 소계획").build();

        when(planRepositoryFacade.findActivePlanById(1L)).thenReturn(validPlan);
        when(subPlanRepositoryFacade.findSubPlansByPlan(validPlan)).thenReturn(
            List.of(validSubPlan));
        when(subPlanRepositoryFacade.findSubPlanBySubPlanId(2L)).thenReturn(otherSubPlan);

        // when & then
        assertThatThrownBy(() -> subPlanService.getSubPlan(1L, 2L))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_NOT_BELONG_TO_PLAN);
    }

    @Test
    @DisplayName("소계획 단건 조회 - 예기치 못한 예외 발생 시")
    void getSubPlan_UnexpectedException() {
        // given
        when(planRepositoryFacade.findActivePlanById(1L)).thenThrow(
            new RuntimeException("DB error"));

        // when & then
        assertThatThrownBy(() -> subPlanService.getSubPlan(1L, 1L))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_READ_FAILED);
    }

    @Test
    @DisplayName("SubPlan 수정 성공")
    void updateSubPlan_Success() {

        SubPlanUpdateResponseDTO expectedResponse = SubPlanUpdateResponseDTO.builder()
            .subPlanId(1L)
            .planId(1L)
            .title(updateRequestDTO.getTitle())
            .isCompleted(updateRequestDTO.getIsCompleted())
            .completedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        given(subPlanRepositoryFacade.findSubPlanBySubPlanId(1L)).willReturn(validSubPlan);

        given(subPlanConverter.toSubPlanUpdateResponseDTO(any(SubPlan.class))).willReturn(
            expectedResponse);

        // when
        SubPlanUpdateResponseDTO actualResponse = subPlanService.updateSubPlan(1L,
            updateRequestDTO);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getSubPlanId()).isEqualTo(expectedResponse.getSubPlanId());
        assertThat(actualResponse.getTitle()).isEqualTo(expectedResponse.getTitle());
        assertThat(actualResponse.getIsCompleted()).isTrue();
        assertThat(actualResponse.getCompletedAt()).isNotNull();
    }


    @Test
    @DisplayName("SubPlan 수정 실패 - 제목 길이 초과")
    void updateSubPlan_Fail_TitleTooLong() {
        // given
        String longTitle = "A".repeat(201);
        SubPlanUpdateRequestDTO longTitleRequest = SubPlanUpdateRequestDTO.builder()
            .title(longTitle)
            .build();

        // when & then
        assertThatThrownBy(() -> subPlanService.updateSubPlan(1L, longTitleRequest))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("SubPlan 수정 실패 - subPlanId 가 null")
    void updateSubPlan_Fail_NullSubPlanId() {
        // when & then
        assertThatThrownBy(() -> subPlanService.updateSubPlan(null, updateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_ID_IS_NULL);
    }

    @Test
    @DisplayName("SubPlan 수정 실패 - 존재하지 않는 subPlan")
    void updateSubPlan_Fail_NotFound() {
        // given
        given(subPlanRepositoryFacade.findSubPlanBySubPlanId(999L))
            .willThrow(new SubPlanException(SubPlanExceptionType.SUB_PLAN_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> subPlanService.updateSubPlan(999L, updateRequestDTO))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_NOT_FOUND);
    }

    @Test
    @DisplayName("소계획 삭제 성공")
    void deleteSubPlan_Success() {

        // given
        Long subPlanId = 1L;

        // when
        subPlanService.deleteSubPlan(subPlanId);

        // then
        verify(subPlanRepositoryFacade).deleteSubPlanBySubPlanId(subPlanId);
    }

    @Test
    @DisplayName("소계획 삭제 - 조회 실패")
    void deleteSubPlan_ReadFailure() {
        // given
        doThrow(new SubPlanException(SubPlanExceptionType.SUB_PLAN_NOT_FOUND))
            .when(subPlanRepositoryFacade)
            .deleteSubPlanBySubPlanId(any());

        assertThatThrownBy(() -> subPlanService.deleteSubPlan(1L))
            // when & then
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_NOT_FOUND);

    }

    @Test
    @DisplayName("소계획 삭제 실패 - 내부 예외 발생")
    void deleteSubPlan_Fail_InternalError() {
        doThrow(new RuntimeException())
            .when(subPlanRepositoryFacade)
            .deleteSubPlanBySubPlanId(any());

        // when & then
        assertThatThrownBy(() -> subPlanService.deleteSubPlan(1L))
            .isInstanceOf(SubPlanException.class)
            .hasFieldOrPropertyWithValue("subPlanExceptionType",
                SubPlanExceptionType.SUB_PLAN_DELETE_FAILED);
    }
}
