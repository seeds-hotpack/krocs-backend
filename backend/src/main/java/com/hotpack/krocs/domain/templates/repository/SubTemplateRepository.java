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

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM sub_templates s
            JOIN templates t ON s.template_id = t.template_id
            JOIN users u ON t.user_id = u.user_id
            WHERE s.sub_template_id = :subTemplateId
              AND t.template_id = :templateId
              AND u.user_id = :userId
              AND s.status = :status
        )
        """, nativeQuery = true)
    boolean existsValidSubTemplate(
        @Param("userId") Long userId,
        @Param("templateId") Long templateId,
        @Param("subTemplateId") Long subTemplateId,
        @Param("status") Status status);
}
