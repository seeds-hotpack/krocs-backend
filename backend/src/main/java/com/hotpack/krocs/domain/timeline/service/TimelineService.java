package com.hotpack.krocs.domain.timeline.service;

import com.hotpack.krocs.domain.timeline.dto.response.TimelineResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface TimelineService {
    TimelineResponseDTO getTimeline(LocalDate startDate, LocalDate endDate, List<String> types, Long userId);
}
