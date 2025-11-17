package com.hotpack.krocs.domain.retrospectives.converter;

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
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveFactorStatisticsDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveStatisticsDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveSummaryDTO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public RetrospectiveSummaryDTO toRetrospectiveSummaryDTO(Retrospective retrospective) {
        List<String> factors = retrospective.getFactors() == null
            ? List.of()
            : List.copyOf(retrospective.getFactors());

        return RetrospectiveSummaryDTO.builder()
            .retrospectiveId(retrospective.getRetrospectiveId())
            .goalId(retrospective.getGoal().getGoalId())
            .goalName(retrospective.getGoal().getTitle())
            .outcome(retrospective.getOutcome())
            .content(retrospective.getContent())
            .factors(factors)
            .createdAt(retrospective.getCreatedAt())
            .build();
    }

    public RetrospectiveStatisticsDTO toStatisticsDTO(List<Retrospective> retrospectives) {
        Map<String, Long> successFactorCounts = new HashMap<>();
        Map<String, Long> failureFactorCounts = new HashMap<>();

        retrospectives.forEach(retrospective -> {
            List<String> factors = retrospective.getFactors();
            if (factors == null || factors.isEmpty()) {
                return;
            }

            Map<String, Long> targetMap = isSuccessOutcome(retrospective.getOutcome())
                ? successFactorCounts
                : failureFactorCounts;
            factors.forEach(factor -> targetMap.merge(factor, 1L, Long::sum));
        });

        return RetrospectiveStatisticsDTO.builder()
            .topSuccessFactors(buildTopFactors(successFactorCounts, true))
            .topFailureFactors(buildTopFactors(failureFactorCounts, false))
            .build();
    }

    private boolean isSuccessOutcome(RetrospectiveOutcome outcome) {
        return outcome == RetrospectiveOutcome.COMPLETE_SUCCESS;
    }

    private List<RetrospectiveFactorStatisticsDTO> buildTopFactors(Map<String, Long> factorCounts,
        boolean isSuccessType) {
        return factorCounts.entrySet().stream()
            .sorted((entry1, entry2) -> {
                int compare = Long.compare(entry2.getValue(), entry1.getValue());
                if (compare == 0) {
                    return entry1.getKey().compareTo(entry2.getKey());
                }
                return compare;
            })
            .limit(3)
            .map(entry -> toFactorStatisticsDTO(entry.getKey(), entry.getValue(), isSuccessType))
            .toList();
    }

    private RetrospectiveFactorStatisticsDTO toFactorStatisticsDTO(String factorKey, long count,
        boolean isSuccessType) {
        String description = isSuccessType
            ? SuccessFactor.valueOf(factorKey).getDescription()
            : FailureFactor.valueOf(factorKey).getDescription();

        return RetrospectiveFactorStatisticsDTO.builder()
            .factor(factorKey)
            .description(description)
            .count(count)
            .build();
    }

}
