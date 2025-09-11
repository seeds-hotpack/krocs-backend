package com.hotpack.krocs.domain.plans.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotpack.krocs.domain.plans.domain.Plan;
import com.hotpack.krocs.domain.plans.domain.SubPlan;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanCreateRequestDTO;
import com.hotpack.krocs.domain.plans.dto.request.SubPlanRequestDTO;
import com.hotpack.krocs.domain.plans.dto.response.SubPlanResponseDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = "spring.profiles.active=dev")
class SubPlanConverterTest {

    private SubPlanConverter subPlanConverter;

    private Plan testPlan;

    @BeforeEach
    void setUp() {
        subPlanConverter = new SubPlanConverter();
        testPlan = Plan.builder()
            .planId(1L)
            .title("테스트 계획")
            .build();
    }

    @Test
    @DisplayName("SubPlanRequestDTO → SubPlan 엔티티로 변환 성공")
    void toEntity_Success() {
        // given
        SubPlanRequestDTO requestDTO = SubPlanRequestDTO.builder()
            .title("서브플랜 제목")
            .build();

        // when
        SubPlan subPlan = subPlanConverter.toEntity(testPlan, requestDTO);

        // then
        assertThat(subPlan).isNotNull();
        assertThat(subPlan.getTitle()).isEqualTo("서브플랜 제목");
        assertThat(subPlan.getPlan()).isEqualTo(testPlan);
        assertThat(subPlan.getIsCompleted()).isFalse(); // 기본값
        assertThat(subPlan.getCompletedAt()).isNull(); // 초기값
    }

    @Test
    @DisplayName("SubPlanCreateRequestDTO → SubPlan 엔티티 리스트 변환 성공")
    void toEntityList_Success() {
        // given
        SubPlanRequestDTO dto1 = SubPlanRequestDTO.builder().title("작업1").build();
        SubPlanRequestDTO dto2 = SubPlanRequestDTO.builder().title("작업2").build();
        SubPlanCreateRequestDTO createRequest = SubPlanCreateRequestDTO.builder()
            .subPlans(List.of(dto1, dto2))
            .build();

        // when
        List<SubPlan> subPlans = subPlanConverter.toSubPlanEntityList(testPlan, createRequest);

        // then
        assertThat(subPlans).hasSize(2);
        assertThat(subPlans.get(0).getTitle()).isEqualTo("작업1");
        assertThat(subPlans.get(1).getTitle()).isEqualTo("작업2");
    }

    @Test
    @DisplayName("SubPlan → SubPlanResponseDTO 변환 성공")
    void toSubPlanResponseDTO_Success() {
        // given
        SubPlan subPlan = SubPlan.builder()
            .subPlanId(10L)
            .title("응답용 제목")
            .isCompleted(true)
            .plan(testPlan)
            .build();

        // when
        SubPlanResponseDTO responseDTO = subPlanConverter.toSubPlanResponseDTO(subPlan);

        // then
        assertThat(responseDTO.getSubPlanId()).isEqualTo(10L);
        assertThat(responseDTO.getTitle()).isEqualTo("응답용 제목");
        assertThat(responseDTO.getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("SubPlan 리스트 → SubPlanResponseDTO 리스트 변환 성공")
    void toSubPlanResponseListDTO_Success() {
        // given
        SubPlan subPlan1 = SubPlan.builder()
            .subPlanId(1L)
            .title("하위계획1")
            .isCompleted(false)
            .plan(testPlan)
            .build();
        SubPlan subPlan2 = SubPlan.builder()
            .subPlanId(2L)
            .title("하위계획2")
            .isCompleted(true)
            .plan(testPlan)
            .build();

        // when
        List<SubPlanResponseDTO> responseDTOs = subPlanConverter.toSubPlanResponseListDTO(
            List.of(subPlan1, subPlan2));

        // then
        assertThat(responseDTOs).hasSize(2);
        assertThat(responseDTOs.get(0).getTitle()).isEqualTo("하위계획1");
        assertThat(responseDTOs.get(1).getIsCompleted()).isTrue();
    }
    //valid 작성 후 적용
//
//    @Test
//    @DisplayName("SubPlanRequestDTO의 title이 null인 경우")
//    void toEntity_WithNullTitle() {
//        // given
//        SubPlanRequestDTO requestDTO = SubPlanRequestDTO.builder()
//            .title(null)
//            .build();
//
//        // when
//        SubPlan subPlan = subPlanConverter.toEntity(testPlan, requestDTO);
//
//        // then
//        assertThat(subPlan).isNotNull();
//        assertThat(subPlan.getTitle()).isNull(); // title null일 수 있음
//    }
//
//    @Test
//    @DisplayName("SubPlanCreateRequestDTO의 subPlans가 빈 리스트일 경우")
//    void toEntityList_WithEmptyList() {
//        // given
//        SubPlanCreateRequestDTO createRequest = SubPlanCreateRequestDTO.builder()
//            .subPlans(List.of())
//            .build();
//
//        // when
//        List<SubPlan> subPlans = subPlanConverter.toSubPlanEntityList(testPlan, createRequest);
//
//        // then
//        assertThat(subPlans).isEmpty();
//    }
}