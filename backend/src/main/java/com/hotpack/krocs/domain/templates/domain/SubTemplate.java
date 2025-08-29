package com.hotpack.krocs.domain.templates.domain;

import com.hotpack.krocs.domain.templates.dto.request.SubTemplateUpdateRequestDTO;
import com.hotpack.krocs.global.common.entity.BaseTimeEntity;
import com.hotpack.krocs.global.common.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sub_templates")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_template_id")
    private Long subTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public void updateFrom(SubTemplateUpdateRequestDTO requestDTO) {
        if (requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }
    }

    public void delete() {
        if (this.status == Status.ACTIVE) {
            this.status = Status.INACTIVE;
        }
    }
}