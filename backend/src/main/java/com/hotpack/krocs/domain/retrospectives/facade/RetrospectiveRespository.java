package com.hotpack.krocs.domain.retrospectives.facade;

import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetrospectiveRespository extends JpaRepository<Retrospective, Long> {

    Retrospective findRetrospectiveByUserAndRetrospectiveIdAndStatus(User user,
        Long retrospectiveId, Status status);

    Page<Retrospective> findByUserAndStatus(User user, Status status, Pageable pageable);

    Page<Retrospective> findByUserAndOutcomeAndStatus(User user, RetrospectiveOutcome outcome,
        Status status, Pageable pageable);

    List<Retrospective> findAllByUserAndStatus(User user, Status status);
}
