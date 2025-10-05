package com.hotpack.krocs.domain.retrospectives.validator;

import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.retrospectives.domain.FailureFactor;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.domain.SuccessFactor;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RetrospectiveValidator {

    public void validateCreate(RetrospectiveCreateRequestDTO requestDTO) {
        String outcome = requestDTO.getOutcome().toUpperCase();
        List<String> factorKeys = requestDTO.getFactors();

        validateFactorSize(factorKeys);
        validateFactorKeysByOutcome(factorKeys, outcome);
        validateOutcomeString(outcome);
    }

    private void validateFactorSize(List<String> factorKeys) {
        if (CollectionUtils.isEmpty(factorKeys) || factorKeys.size() > 3) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_FACTORS_SIZE);
        }
    }

    private void validateOutcomeString(String outcome) {
        try {
            RetrospectiveOutcome.valueOf(outcome.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_OUTCOME_KEY);
        }
    }

    private void validateFactorKeysByOutcome(List<String> factorKeys, String outcome) {
        boolean allKeysValid;
        switch (outcome) {
            case "COMPLETE_SUCCESS":
                allKeysValid = factorKeys.stream().allMatch(SuccessFactor::isValidKey);
                break;
            case "COMPLETE_FAILURE":
            case "RETRY_FAILURE":
                allKeysValid = factorKeys.stream().allMatch(FailureFactor::isValidKey);
                break;
            default:
                throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_OUTCOME_KEY);
        }

        if (!allKeysValid) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_FACTORS_MISMATCH_OUTCOME);
        }
    }

    public void validateDelete(Goal goal, Long requestUserId, Long retroId) {
        if (!goal.getUser().getUserId().equals(requestUserId)) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_NOT_FOUND);
        } else if (!goal.getUser().getUserId().equals(retroId)){
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_NOT_FOUND);
        }
    }
}