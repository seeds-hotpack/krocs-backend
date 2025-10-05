package com.hotpack.krocs.domain.retrospectives.facade;

import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetrospectiveRespository extends JpaRepository<Retrospective, Long> {

    Retrospective findRetrospectiveByUserAndRetrospectiveIdAndStatus(User user,
        Long retrospectiveId, Status status);
}
