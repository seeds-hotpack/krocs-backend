package com.hotpack.krocs.domain.retrospectives.converter;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.retrospectives.domain.FailureFactor;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.domain.SuccessFactor;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.FactorDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RetrospectiveConverter {

    public Retrospective toEntity(RetrospectiveCreateRequestDTO requestDTO, User user, Goal goal) {
        RetrospectiveOutcome outcome = RetrospectiveOutcome.valueOf(requestDTO.getOutcome().toUpperCase());

        String contentToSave = null;
        if (requestDTO.getFactors() != null && requestDTO.getFactors().contains("ETC")) {
            contentToSave = requestDTO.getContent();
        }
        return Retrospective.builder()
            .user(user)
            .goal(goal)
            .outcome(outcome)
            .content(contentToSave)
            .factors(requestDTO.getFactors())
            .build();
    }

    public RetrospectiveCreateResponseDTO toCreateResponseDTO(Retrospective retrospective) {
        List<FactorDTO> factorDTOs = getFactorsByType(retrospective.isSuccess());

        return RetrospectiveCreateResponseDTO.builder()
            .retrospectiveId(retrospective.getRetrospectiveId())
            .goalId(retrospective.getGoal().getGoalId())
            .outcome(retrospective.getOutcome())
            .isSuccess(retrospective.isSuccess())
            .factors(factorDTOs)
            .content(retrospective.getContent())
            .createdAt(retrospective.getCreatedAt())
            .updatedAt(retrospective.getUpdatedAt())
            .build();
    }

    private List<FactorDTO> getFactorsByType(boolean isSuccess) {
        if (isSuccess) {
            return Arrays.stream(SuccessFactor.values())
                .map(f -> FactorDTO.builder().key(f.name()).description(f.getDescription()).build())
                .toList();
        } else {
            return Arrays.stream(FailureFactor.values())
                .map(f -> FactorDTO.builder().key(f.name()).description(f.getDescription()).build())
                .toList();
        }
    }
}