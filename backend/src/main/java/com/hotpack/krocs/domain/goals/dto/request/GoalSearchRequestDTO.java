package com.hotpack.krocs.domain.goals.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalSearchRequestDTO {
    private LocalDate searchDate;
    private String keyword;
    private String status; // "IN_PROGRESS", "COMPLETED", "EXPIRED" 문자열로
}
