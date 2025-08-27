package com.hotpack.krocs.domain.templates.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.templates.converter.TemplateConverter;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.TemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.TemplateException;
import com.hotpack.krocs.domain.templates.exception.TemplateExceptionType;
import com.hotpack.krocs.domain.templates.facade.TemplateRepositoryFacade;
import com.hotpack.krocs.domain.templates.validator.TemplateValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.Priority;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepositoryFacade templateRepositoryFacade;

    @Spy
    private TemplateValidator templateValidator = new TemplateValidator(); // 수동 생성

    @Mock
    private TemplateConverter templateConverter;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private TemplateCreateRequestDTO validCreateRequestDTO;
    private TemplateUpdateRequestDTO validUpdateRequestDTO;
    private Template validTemplate;
    private TemplateCreateResponseDTO validCreateResponseDTO;
    private TemplateResponseDTO validResponseDTO;
    private SubTemplateResponseDTO subTemplateResponseDTO;

    @BeforeEach
    void setUp() {
        validCreateRequestDTO = TemplateCreateRequestDTO.builder()
            .title("공부 루틴")
            .priority(Priority.HIGH)
            .duration(30)
            .build();

        validUpdateRequestDTO = TemplateUpdateRequestDTO.builder()
            .title("수정된 루틴")
            .priority(Priority.LOW)
            .duration(60)
            .build();

        validTemplate = Template.builder()
            .templateId(1L)
            .title("공부 루틴")
            .priority(Priority.HIGH)
            .duration(30)
            .subTemplates(new ArrayList<>())
            .build();

        validCreateResponseDTO = TemplateCreateResponseDTO.builder()
            .templateId(1L)
            .title("공부 루틴")
            .priority(Priority.HIGH)
            .duration(30)
            .build();

        validResponseDTO = TemplateResponseDTO.builder()
            .templateId(1L)
            .title("공부 루틴")
            .priority(Priority.HIGH)
            .duration(30)
            .subTemplates(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    // ======= CREATE =======


    @Test
    @DisplayName("템플릿 생성 성공 테스트")
    void createTemplate_Success() {
        // given
        when(templateConverter.toEntity(eq(validCreateRequestDTO), any(User.class))).thenReturn(
            validTemplate);
        when(templateRepositoryFacade.save(validTemplate)).thenReturn(validTemplate);
        when(templateConverter.toCreateResponseDTO(validTemplate)).thenReturn(
            validCreateResponseDTO);

        // when
        TemplateCreateResponseDTO result = templateService.createTemplate(validCreateRequestDTO,
            3L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("공부 루틴");
        assertThat(result.getDuration()).isEqualTo(30);
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("DTO 기본값 테스트 - priority는 명시하지 않으면 MEDIUM으로 설정된다")
    void createTemplateRequestDTO_DefaultPriority_ShouldBeMedium() {
        // given
        TemplateCreateRequestDTO result = TemplateCreateRequestDTO.builder()
            .title("공부 루틴")
            .duration(30)
            .build();

        // then
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }


    @Test
    @DisplayName("템플릿 생성 실패 - title이 공백인 경우")
    void createTemplate_Fail_BlankTitle() {
        // given
        TemplateCreateRequestDTO request = TemplateCreateRequestDTO.builder()
            .title("")            // 빈 제목
            .priority(Priority.HIGH)
            .duration(10)
            .build();

        // when & then
        TemplateException ex = assertThrows(
            TemplateException.class,
            () -> templateService.createTemplate(request, 1L)
        );

        assertThat(ex.getTemplateExceptionType())
            .isEqualTo(TemplateExceptionType.TEMPLATE_TITLE_EMPTY);

        // 그리고 validateTitle 메서드가 실제로 호출됐는지 확인해 줄 수도 있어요
        verify(templateValidator).validateTitle("");
    }

    @Test
    @DisplayName("템플릿 생성 실패 - duration이 음수인 경우")
    void createTemplate_Fail_NegativeDuration() {
        // given
        TemplateCreateRequestDTO request = TemplateCreateRequestDTO.builder()
            .title("공부")
            .priority(Priority.HIGH)
            .duration(-10)
            .build();

        // when & then
        TemplateException exception = assertThrows(TemplateException.class,
            () -> templateService.createTemplate(request, 1L));

        assertThat(exception.getTemplateExceptionType())
            .isEqualTo(TemplateExceptionType.TEMPLATE_DURATION_INVALID);
    }

    // ======= READ =======
    @Test
    @DisplayName("템플릿 전체 조회 성공 - 제목 없이 조회")
    void getTemplates_Success_WithoutTitle() {
        // given

        when(templateRepositoryFacade.findAllActiveTemplates())
            .thenReturn(List.of(validTemplate));

        when(templateConverter.toTemplateResponseDTO(validTemplate))
            .thenReturn(validResponseDTO);

        // when
        List<TemplateResponseDTO> result = templateService.getTemplatesByUserAndTitle(1L, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("공부 루틴");
    }

    @Test
    @DisplayName("템플릿 검색 조회 성공 - 제목 키워드 포함")
    void getTemplates_Success_WithKeyword() {
        // when
        when(templateRepositoryFacade.findActiveTemplatesByTitle("공부"))
            .thenReturn(List.of(validTemplate));

        when(templateConverter.toTemplateResponseDTO(validTemplate))
            .thenReturn(validResponseDTO);

        // then
        List<TemplateResponseDTO> result = templateService.getTemplatesByUserAndTitle(1L, "공부");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("공부");
    }

    @Test
    @DisplayName("템플릿 검색 조회 - 결과가 없는 경우")
    void getTemplates_EmptyResult() {

        // then
        List<TemplateResponseDTO> result = templateService.getTemplatesByUserAndTitle(1L, "운동");
        assertThat(result).isEmpty();
    }

    // ======= UPDATE =======

    @Test
    @DisplayName("템플릿 수정 성공")
    void updateTemplate_Success() {
        // given
        Template existed = validTemplate;
        TemplateUpdateRequestDTO dto = validUpdateRequestDTO;

        Template updatedEntity = Template.builder()
            .templateId(1L)
            .title(dto.getTitle())
            .priority(dto.getPriority())
            .duration(dto.getDuration())
            .subTemplates(new ArrayList<>())
            .build();

        TemplateResponseDTO validResponseDTO = TemplateResponseDTO.builder()
            .templateId(1L)
            .title("수정된 루틴")
            .priority(Priority.LOW)
            .duration(60)
            .subTemplates(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(templateConverter.toTemplateResponseDTO(updatedEntity))
            .thenReturn(validResponseDTO);

        when(templateRepositoryFacade.findActiveTemplateByTemplateId(1L))
            .thenReturn(existed).thenReturn(updatedEntity);

        // when
        TemplateResponseDTO response = templateService.updateTemplate(1L, 1L, dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(dto.getTitle());
        assertThat(response.getPriority()).isEqualTo(dto.getPriority());
        assertThat(response.getDuration()).isEqualTo(dto.getDuration());
    }


    @Test
    @DisplayName("템플릿 수정 실패 - title이 최대값(200자) 초과")
    void updateTemplate_Fail_TitleExceedsLimit() {
        // given
        Template existingTemplate = Template.builder()
            .templateId(1L)
            .title("기존 루틴")
            .priority(Priority.MEDIUM)
            .duration(30)
            .subTemplates(List.of())
            .build();

        String overLengthTitle = "a".repeat(201); // 201자짜리 title
        TemplateUpdateRequestDTO requestDTO = TemplateUpdateRequestDTO.builder()
            .title(overLengthTitle)
            .build();

        // when
        TemplateException exception = catchThrowableOfType(
            () -> templateService.updateTemplate(1L, 1L, requestDTO),
            TemplateException.class
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getTemplateExceptionType()).isEqualTo(
            TemplateExceptionType.TEMPLATE_TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("템플릿 수정 실패 - 존재하지 않는 템플릿")
    void updateTemplate_Fail_TemplateNotFound() {
        // when
        when(templateRepositoryFacade.findActiveTemplateByTemplateId(1L))
            .thenThrow(new TemplateException(TemplateExceptionType.TEMPLATE_NOT_FOUND));

        TemplateException exception = catchThrowableOfType(
            () -> templateService.updateTemplate(1L, 1L, validUpdateRequestDTO),
            TemplateException.class
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getTemplateExceptionType()).isEqualTo(
            TemplateExceptionType.TEMPLATE_NOT_FOUND);
    }


    // D - 템플릿 삭제 성공
    @Test
    @DisplayName("템플릿 삭제 성공")
    void deleteTemplate_Success() {
        // when
        when(templateRepositoryFacade.findActiveTemplateByTemplateId(1L))
            .thenReturn(validTemplate);

        // then
        assertThatCode(() -> templateService.deleteTemplate(1L, 1L))
            .doesNotThrowAnyException();

        verify(templateRepositoryFacade).deleteActiveTemplate(validTemplate);
    }

    @Test
    @DisplayName("템플릿 삭제 실패 - 존재하지 않는 템플릿")
    void deleteTemplate_Fail_TemplateNotFound() {
        // when

        when(templateRepositoryFacade.findActiveTemplateByTemplateId(1L))
            .thenThrow(new TemplateException(TemplateExceptionType.TEMPLATE_NOT_FOUND));

        TemplateException exception = catchThrowableOfType(
            () -> templateService.deleteTemplate(1L, 1L),
            TemplateException.class
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getTemplateExceptionType()).isEqualTo(
            TemplateExceptionType.TEMPLATE_NOT_FOUND);
    }


}