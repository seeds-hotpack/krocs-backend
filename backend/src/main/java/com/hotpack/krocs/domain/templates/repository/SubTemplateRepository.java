package com.hotpack.krocs.domain.templates.repository;

import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTemplateRepository extends JpaRepository<SubTemplate, Long> {

    List<SubTemplate> findSubTemplatesByTemplateAndStatus(Template template, Status status);

    SubTemplate findSubTemplateBySubTemplateIdAndStatus(Long subTemplateId, Status status);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM SubTemplate s
        WHERE s.subTemplateId = :subTemplateId
          AND s.template.templateId = :templateId
          AND s.template.user.userId = :userId
          AND s.status = :status
        """)
    boolean existsValidSubTemplate(
        @Param("userId") Long userId,
        @Param("templateId") Long templateId,
        @Param("subTemplateId") Long subTemplateId,
        @Param("status") Status status);
}
