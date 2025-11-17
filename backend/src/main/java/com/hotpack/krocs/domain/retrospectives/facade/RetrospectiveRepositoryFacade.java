package com.hotpack.krocs.domain.retrospectives.facade;


import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.global.common.entity.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveRepositoryFacade {

    private final RetrospectiveRespository retroRespository;

    @Transactional
    public Retrospective saveRetrospective(Retrospective retro) {
        return retroRespository.save(retro);
    }

    public Retrospective findActiveRetrospectiveByUserAndRetrospectiveId(User user, Long retrospectiveId) {
        Retrospective retro = retroRespository.findRetrospectiveByUserAndRetrospectiveIdAndStatus(user,
            retrospectiveId, Status.ACTIVE);
        if (retro == null) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_NOT_FOUND);
        }
        return retro;
    }

    public Page<Retrospective> findRetrospectivesByUser(User user, Pageable pageable) {
        return retroRespository.findByUserAndStatus(user, Status.ACTIVE, pageable);
    }

    public Page<Retrospective> findRetrospectivesByUserAndOutcome(User user, RetrospectiveOutcome outcome,
        Pageable pageable) {
        return retroRespository.findByUserAndOutcomeAndStatus(user, outcome, Status.ACTIVE, pageable);
    }

    public List<Retrospective> findAllActiveRetrospectivesByUser(User user) {
        return retroRespository.findAllByUserAndStatus(user, Status.ACTIVE);
    }

    public void delete(Retrospective retrospective) {
        retrospective.delete();
    }
}
