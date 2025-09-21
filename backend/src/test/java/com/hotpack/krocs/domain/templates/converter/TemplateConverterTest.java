package com.hotpack.krocs.domain.templates.converter;

import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.domain.templates.dto.request.TemplateCreateRequestDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateCreateResponseDTO;
import com.hotpack.krocs.domain.templates.dto.response.TemplateResponseDTO;
import com.hotpack.krocs.global.common.entity.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateConverterTest {

    private TemplateConverter templateConverter;

    private TemplateCreateRequestDTO validRequestDTO;
    private Template validTemplate;

    @BeforeEach
    void setUp() {
        templateConverter = new TemplateConverter();

        validRequestDTO = TemplateCreateRequestDTO.builder()
                .title("테스트 템플릿")
                .priority(Priority.HIGH)
                .duration(30)
                .build();

        validTemplate = Template.builder()
                .templateId(1L)
                .title("테스트 템플릿")
                .priority(Priority.HIGH)
                .duration(30)
                .subTemplates(List.of(
                        SubTemplate.builder().subTemplateId(1L).title("서브1").build(),
                        SubTemplate.builder().subTemplateId(2L).title("서브2").build()
                ))
                .build();
    }

    @Test
    @DisplayName("CreateTemplateRequestDTO를 Template 엔티티로 변환")
    void toEntity_Success() {
        // when
        Template result = templateConverter.toEntity(validRequestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 템플릿");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(result.getDuration()).isEqualTo(30);
        assertTrue(result.getSubTemplates().isEmpty()); // subTemplates는 요청에서 없음
    }

    @Test
    @DisplayName("Template 엔티티를 CreateTemplateResponseDTO로 변환")
    void toCreateResponseDTO_Success() {
        // given
        // when
        TemplateCreateResponseDTO result = templateConverter.toCreateResponseDTO(validTemplate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 템플릿");
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(result.getDuration()).isEqualTo(30);
        assertThat(result.getSubTemplates()).hasSize(2);
        assertThat(result.getSubTemplates().get(0).getSubTemplateId()).isEqualTo(1L);
        assertThat(result.getSubTemplates().get(0).getTitle()).isEqualTo("서브1");
    }

    @Test
    @DisplayName("최소 데이터로 DTO를 Template 엔티티로 변환")
    void toEntity_MinimalData() {
        // given
        TemplateCreateRequestDTO minimalRequest = TemplateCreateRequestDTO.builder()
                .title("간단 템플릿")
                .duration(7)
                .build();

        // when
        Template result = templateConverter.toEntity(minimalRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("간단 템플릿");
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM); // 기본값
        assertThat(result.getDuration()).isEqualTo(7);
    }

    @Test
    @DisplayName("null 필드를 포함한 Template 엔티티를 응답 DTO로 변환")
    void toCreateResponseDTO_WithNullValues() {
        // given
        Template templateWithNulls = Template.builder()
                .templateId(2L)
                .title("null 템플릿")
                .priority(null)
                .duration(15)
                .subTemplates(null)
                .build();

        // when
        TemplateCreateResponseDTO result = templateConverter.toCreateResponseDTO(templateWithNulls);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTemplateId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("null 템플릿");
        assertThat(result.getPriority()).isNull();
        assertThat(result.getDuration()).isEqualTo(15);
        assertThat(result.getSubTemplates()).isEmpty();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }


    @Test
    @DisplayName("Template 엔티티를 TemplateResponseDTO로 변환")
    void toTemplateResponseDTO_Success() {
        // given
        Template template = Template.builder()
                .templateId(1L)
                .title("완성된 템플릿")
                .priority(Priority.LOW)
                .duration(14)
                .subTemplates(List.of(
                        SubTemplate.builder().subTemplateId(10L).title("소목표1").build(),
                        SubTemplate.builder().subTemplateId(11L).title("소목표2").build()
                ))
                .build();

        // when
        TemplateResponseDTO result = templateConverter.toTemplateResponseDTO(template);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("완성된 템플릿");
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getDuration()).isEqualTo(14);
        assertThat(result.getSubTemplates()).hasSize(2);
        assertThat(result.getSubTemplates().get(0).getSubTemplateId()).isEqualTo(10L);
        assertThat(result.getSubTemplates().get(0).getTitle()).isEqualTo("소목표1");
    }

    @Test
    @DisplayName("비어있는 SubTemplate 리스트를 포함한 Template 엔티티를 응답 DTO로 변환")
    void toCreateResponseDTO_WithEmptySubTemplates() {
        // given
        Template templateWithEmptyList = Template.builder()
            .templateId(3L)
            .title("빈 서브템플릿 템플릿")
            .subTemplates(List.of()) // 명시적으로 비어있는 리스트
            .build();

        // when
        TemplateCreateResponseDTO result = templateConverter.toCreateResponseDTO(templateWithEmptyList);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSubTemplates()).isNotNull();
        assertThat(result.getSubTemplates()).isEmpty();
    }


}
