package com.hotpack.krocs.domain.retrospectives.service;

import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;

public interface RetrospectiveService {

    // 회고 생성 API
    RetrospectiveCreateResponseDTO createRetrospective(Long userId, Long goalId, RetrospectiveCreateRequestDTO requestDTO);
}
