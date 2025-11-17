package com.hotpack.krocs.domain.retrospectives.service;

import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.AllFactorsResponseDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveMyPageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface RetrospectiveService {

    void deleteRetrospective(Long userId, Long goalId, Long retroId);

    RetrospectiveCreateResponseDTO createRetrospective(Long userId, Long goalId, RetrospectiveCreateRequestDTO requestDTO);

    AllFactorsResponseDTO getFactors ();

    RetrospectiveMyPageResponseDTO getMyPageRetrospectives(Long userId, String outcome, Pageable pageable);
}
