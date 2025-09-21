package com.hotpack.krocs.domain.templates.repository;

import com.hotpack.krocs.domain.templates.domain.Template;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findTemplatesByTitleContainingIgnoreCaseAndUser_userIdAndStatus(String title,
        Long userId, Status status);

    List<Template> findAllTemplatesByUser_UserIdAndStatus(Long userId, Status status);

    Template findTemplateByTemplateIdAndUser_UserIdAndStatus(Long templateId, Long userId,
        Status status);

    Page<Template> findTemplatesByTitleContainingIgnoreCaseAndUser_userIdAndStatus(String title,
        Long userId, Status status, Pageable pageable);

    Page<Template> findAllTemplatesByUser_UserIdAndStatus(Long userId, Status status, Pageable pageable);

}