package com.hotpack.krocs.domain.templates.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hotpack.krocs.domain.templates.converter.SubTemplateConverter;
import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateDeleteResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.SubTemplateResponseDTO;
import com.hotpack.krocs.domain.templates.exception.SubTemplateException;
import com.hotpack.krocs.domain.templates.exception.SubTemplateExceptionType;
import com.hotpack.krocs.domain.templates.facade.SubTemplateRepositoryFacade;
import com.hotpack.krocs.domain.templates.facade.TemplateRepositoryFacade;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.domain.enums.AccountType;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import com.hotpack.krocs.global.common.entity.Priority;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubTemplateServiceTest {

    @Spy
    private SubTemplateConverter subTemplateConverter;
    @Mock
    private SubTemplateRepositoryFacade subTemplateRepositoryFacade;
    @Mock
    private UserRepositoryFacade userRepositoryFacade;
    @Mock
    private TemplateRepositoryFacade templateRepositoryFacade;

    @InjectMocks
    private SubTemplateServiceImpl subTemplateService;

    private SubTemplateCreateRequestDTO validCreateRequestDTO;
    private SubTemplateRequestDTO validSubTemplateRequestDTO;
    private SubTemplateUpdateRequestDTO validSubTemplateUpdateRequestDTO;
    private SubTemplate validSubTemplate;
    private Template validTemplate;
    private User user;


    @BeforeEach
    void setup() {
        // given
        user = User.builder()
            .name("박성열")
            .email("qkrtjdduf@example.com")
            .accountId("local-" + UUID.randomUUID())
            .accountType(AccountType.LOCAL)
            .build();

        validTemplate = Template.builder()
            .templateId(1L)
            .priority(Priority.HIGH)
            .duration(10)
            .build();

        validSubTemplateRequestDTO = SubTemplateRequestDTO.builder()
            .title("테스트입니다")
            .build();

        validCreateRequestDTO = SubTemplateCreateRequestDTO.builder()
            .subTemplates(List.of(validSubTemplateRequestDTO))
            .build();

        validSubTemplate = SubTemplate.builder()
            .subTemplateId(1L)
            .template(validTemplate)
            .title("테스트입니다")
            .build();

        validSubTemplateUpdateRequestDTO = SubTemplateUpdateRequestDTO.builder()
            .title("수정되었습니다")
            .build();
    }

    // ======= CREATE =======

    @Test
    @DisplayName("서브 템플릿 생성 성공")
    void createSubTemplates_Success() {
        // when
        when(templateRepositoryFacade.findActiveParentTemplateByTemplateIdAndUserId(1L,
            1L)).thenReturn(
            validTemplate);
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(subTemplateRepositoryFacade.saveAll(any())).thenReturn(List.of(validSubTemplate));

        SubTemplateCreateResponseDTO responseDTO = subTemplateService.createSubTemplates(1L,
            validCreateRequestDTO, 1L);

        // then
        SubTemplateResponseDTO subTemplateResponseDTO = responseDTO.getSubTemplates().getFirst();
        assertThat(subTemplateResponseDTO.getSubTemplateId()).isEqualTo(1L);
        assertThat(subTemplateResponseDTO.getTemplateId()).isEqualTo(1L);
        assertThat(subTemplateResponseDTO.getTitle()).isEqualTo("테스트입니다");
    }

    @Test
    @DisplayName("서브 템플릿 생성 실패 - templateId가 null인 경우")
    void createSubTemplates_templateIdIsNull() {
        // when
        SubTemplateException exception = assertThrows(SubTemplateException.class,
            () -> subTemplateService.createSubTemplates(null, validCreateRequestDTO, 1L));

        // then
        assertThat(exception.getSubTemplateExceptionType()).isEqualTo(
            SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_ID_IS_NULL);
    }

    @Test
    @DisplayName("서브 템플릿 생성 실패 - 부모 템플릿이 존재하지 않는 경우")
    void createSubTemplates_templateNotFound() {
        // given
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(templateRepositoryFacade.findActiveParentTemplateByTemplateIdAndUserId(1L, 1L))
            .thenThrow(
                new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_NOT_FOUND));

        // when & then
        SubTemplateException exception = assertThrows(SubTemplateException.class,
            () -> subTemplateService.createSubTemplates(1L, validCreateRequestDTO, 1L));

        assertThat(exception.getSubTemplateExceptionType()).isEqualTo(
            SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_NOT_FOUND);
    }

    @Test
    @DisplayName("서브 템플릿 생성 실패 - 예상치 못한 오류 발생")
    void createSubTemplates_UnknownException() {
        // when
        when(userRepositoryFacade.findActiveUserByUserId(1L)).thenReturn(user);
        when(templateRepositoryFacade.findActiveParentTemplateByTemplateIdAndUserId(1L,
            1L)).thenThrow(
            new RuntimeException());

        SubTemplateException exception = assertThrows(SubTemplateException.class,
            () -> subTemplateService.createSubTemplates(1L, validCreateRequestDTO, 1L));

        // then
        assertThat(exception.getSubTemplateExceptionType()).isEqualTo(
            SubTemplateExceptionType.SUB_TEMPLATE_CREATION_FAILED);
    }

    // ======= READ =======

    @Test
    @DisplayName("서브 템플릿 전체 조회 - 성공")
    void getSubTemplates_Success() {
        // when
        when(templateRepositoryFacade.findActiveParentTemplateByTemplateIdAndUserId(1L,
            1L)).thenReturn(
            validTemplate);
        when(
            subTemplateRepositoryFacade.findActiveSubTemplatesByTemplate(validTemplate)).thenReturn(
            List.of(validSubTemplate));
        List<SubTemplateResponseDTO> subTemplateResponseDTOs = subTemplateService.getSubTemplates(
            1L, 1L);

        // then
        assertThat(subTemplateResponseDTOs).hasSize(1);
        assertThat(subTemplateResponseDTOs.getFirst().getSubTemplateId()).isEqualTo(1L);
        assertThat(subTemplateResponseDTOs.getFirst().getTemplateId()).isEqualTo(1L);
        assertThat(subTemplateResponseDTOs.getFirst().getTitle()).isEqualTo("테스트입니다");
    }

    @Test
    @DisplayName("서브 템플릿 전체 조회 - templateId가 null인 경우")
    void getSubTemplates_TemplateIdIsNull() {

        // when & then
        assertThatThrownBy(() -> subTemplateService.getSubTemplates(null, 1L))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_TEMPLATE_ID_IS_NULL);
            });
    }

    @Test
    @DisplayName("서브 템플릿 전체 조회 - subTemplate을 찾지 못한 경우")
    void getSubTemplates_SubTemplateNotFound() {
        when(templateRepositoryFacade.findActiveParentTemplateByTemplateIdAndUserId(1L,
            1L)).thenReturn(validTemplate);
        when(
            subTemplateRepositoryFacade.findActiveSubTemplatesByTemplate(validTemplate)).thenReturn(
            null);
        // when & then
        assertThatThrownBy(() -> subTemplateService.getSubTemplates(1L, 1L))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_FOUND_FAILED);
            });
    }

    // ======= DELETE =======

    @Test
    @DisplayName("서브 템플릿 삭제 - 성공")
    void deleteSubTemplate_Success() {
        // when
        when(subTemplateRepositoryFacade.existsValidSubTemplate(1L, 1L, 1L)).thenReturn(true);
        when(subTemplateRepositoryFacade.deleteActiveSubTemplateBySubTemplateId(1L)).thenReturn(1L);
        SubTemplateDeleteResponseDTO responseDTO = subTemplateService.deleteSubTemplate(1L, 1L, 1L);

        // then
        assertThat(responseDTO.getSubTemplateId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("서브 템플릿 삭제 - subTemplate이 null인 경우")
    void deleteSubTemplate_subTemplateIsNull() {
        // given
        when(subTemplateRepositoryFacade.existsValidSubTemplate(1L, 1L, 1L)).thenReturn(true);
        when(subTemplateRepositoryFacade.deleteActiveSubTemplateBySubTemplateId(1L)).thenThrow(
            new SubTemplateException(SubTemplateExceptionType.SUB_TEMPLATE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> subTemplateService.deleteSubTemplate(1L, 1L, 1L))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("서브 템플릿 삭제 - subTemplateId가 null인 경우")
    void deleteSubTemplate_subTemplateIdIsNull() {
        // when & then
        assertThatThrownBy(() -> subTemplateService.deleteSubTemplate(null, 1L, 1L))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_SUB_TEMPLATE_ID_IS_NULL);
            });
    }

    // ======= UPDATE =======

    @Test
    @DisplayName("서브 템플릿 수정 - 성공")
    void updateSubTemplate_Success() {
        // given
        when(subTemplateRepositoryFacade.existsValidSubTemplate(1L, 1L, 1L)).thenReturn(true);
        SubTemplate updatedSubTemplate = SubTemplate.builder()
            .subTemplateId(1L)
            .title("수정되었습니다")
            .template(validTemplate)
            .build();

        when(subTemplateRepositoryFacade.updateActiveSubTemplateBySubTemplateId(1L,
            validSubTemplateUpdateRequestDTO)).thenReturn(updatedSubTemplate);

        // when
        SubTemplateResponseDTO responseDTO = subTemplateService.updateSubTemplate(1L, 1L, 1L,
            validSubTemplateUpdateRequestDTO);

        // then
        assertThat(responseDTO.getTitle()).isEqualTo("수정되었습니다");
        assertThat(responseDTO.getSubTemplateId()).isEqualTo(1L);
        assertThat(responseDTO.getTemplateId()).isEqualTo(validTemplate.getTemplateId());
    }

    @Test
    @DisplayName("서브 템플릿 수정 - subTemplateId가 null인 경우")
    void updateSubTemplate_subTemplateIdIsNull() {
        // when & then
        assertThatThrownBy(
            () -> subTemplateService.updateSubTemplate(null, 1L, 1L,
                validSubTemplateUpdateRequestDTO))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_SUB_TEMPLATE_ID_IS_NULL);
            });
    }

    @Test
    @DisplayName("서브 템플릿 수정 - 예상치 못한 예외")
    void updateSubTemplate_unknownException() {
        // given
        when(subTemplateRepositoryFacade.updateActiveSubTemplateBySubTemplateId(1L,
            validSubTemplateUpdateRequestDTO)).thenThrow(new RuntimeException());
        when(subTemplateRepositoryFacade.existsValidSubTemplate(1L, 1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(
            () -> subTemplateService.updateSubTemplate(1L, 1L, 1L,
                validSubTemplateUpdateRequestDTO))
            .isInstanceOf(SubTemplateException.class)
            .satisfies(ex -> {
                SubTemplateException e = (SubTemplateException) ex;
                assertThat(e.getSubTemplateExceptionType()).isEqualTo(
                    SubTemplateExceptionType.SUB_TEMPLATE_UPDATE_FAILED);
            });
    }
}