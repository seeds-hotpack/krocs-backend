package com.hotpack.krocs.domain.retrospectives.facade;

import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetrospectiveRespository extends JpaRepository<Retrospective, Long> {

}
