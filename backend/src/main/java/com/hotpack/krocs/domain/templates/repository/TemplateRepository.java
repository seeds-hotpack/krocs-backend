package com.hotpack.krocs.domain.templates.repository;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findTemplatesByTitleContainingIgnoreCaseAndStatus(String title, Status status);

    List<Template> findAllTemplatesByStatus(Status status);

    Template findTemplateByTemplateIdAndStatus(Long templateId, Status status);

    boolean existsTemplateByTitleAndStatus(String title, Status status);
}