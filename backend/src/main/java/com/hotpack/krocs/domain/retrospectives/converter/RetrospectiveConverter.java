package com.hotpack.krocs.domain.retrospectives.converter;

import static java.util.stream.Collectors.toList;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.retrospectives.dto.response.AllFactorsResponseDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
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
        if (requestDTO.getFactors() == null) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_FACTORS_SIZE);
        }

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
        List<FactorDTO> factorDTOs = getFactorsByType(retrospective);

        return RetrospectiveCreateResponseDTO.builder()
            .retrospectiveId(retrospective.getRetrospectiveId())
            .goalId(retrospective.getGoal().getGoalId())
            .outcome(retrospective.getOutcome())
            .factors(factorDTOs)
            .content(retrospective.getContent())
            .createdAt(retrospective.getCreatedAt())
            .updatedAt(retrospective.getUpdatedAt())
            .build();
    }

    public FactorDTO toFactorDTO(SuccessFactor factor) {
        return FactorDTO.builder()
            .key(factor.name())
            .description(factor.getDescription())
            .build();
    }

    public FactorDTO toFactorDTO(FailureFactor factor) {
        return FactorDTO.builder()
            .key(factor.name())
            .description(factor.getDescription())
            .build();
    }


    public AllFactorsResponseDTO toAllFactorsResponseDTO() {
        List<FactorDTO> successFactorDTOs = Arrays.stream(SuccessFactor.values())
            .map(this::toFactorDTO)
            .toList();

        List<FactorDTO> failureFactorDTOs = Arrays.stream(FailureFactor.values())
            .map(this::toFactorDTO)
            .toList();

        // 최종 응답 DTO를 빌드하여 반환
        return AllFactorsResponseDTO.builder()
            .successFactors(successFactorDTOs)
            .failureFactors(failureFactorDTOs)
            .build();
    }

    private List<FactorDTO> getFactorsByType(Retrospective retrospective) {
        List<String> selectedFactorKeys = retrospective.getFactors();

        return selectedFactorKeys.stream()
            .map(key -> {
                try {
                    switch (retrospective.getOutcome()) {
                        case COMPLETE_SUCCESS:
                            SuccessFactor successFactor = SuccessFactor.valueOf(key);
                            return toFactorDTO(successFactor);

                        case COMPLETE_FAILURE:
                        case RETRY_FAILURE:
                            FailureFactor failureFactor = FailureFactor.valueOf(key);
                            return toFactorDTO(failureFactor);

                        default:
                            throw new IllegalStateException("Unexpected outcome: " + retrospective.getOutcome());
                    }
                } catch (IllegalArgumentException e) {
                    throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME);
                }
            })
            .toList();
    }
}