package com.hotpack.krocs.domain.retrospectives.facade;


import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveRepositoryFacade {

    private final RetrospectiveRespository retroRespository;


    @Transactional
    public Retrospective saveRetrospective(Retrospective retro){
        return retroRespository.save(retro);
    }
}
