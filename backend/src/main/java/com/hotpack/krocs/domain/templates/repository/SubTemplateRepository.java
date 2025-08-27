package com.hotpack.krocs.domain.templates.repository;

import com.hotpack.krocs.domain.templates.domain.SubTemplate;
import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTemplateRepository extends JpaRepository<SubTemplate, Long> {

    List<SubTemplate> findSubTemplatesByTemplateAndStatus(Template template, Status status);

    SubTemplate findSubTemplateBySubTemplateIdAndStatus(Long subTemplateId, Status status);
}
