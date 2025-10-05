package com.hotpack.krocs.domain.retrospectives.validator;


import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.retrospectives.domain.FailureFactor;
import com.hotpack.krocs.domain.retrospectives.domain.SuccessFactor;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RetrospectiveValidator {

    public void validateCreate(Goal goal, RetrospectiveCreateRequestDTO requestDTO) {
        validateGoalState(goal);

        String outcome = requestDTO.getOutcome().toUpperCase();
        if (!"SKIP".equals(outcome)) {
            validateFactors(requestDTO.getFactors(), requestDTO.isSuccess());
        } else{
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_OUTCOME_KEY);
        }
    }

    private void validateGoalState(Goal goal) {
        if (Boolean.TRUE.equals(goal.getIsCompleted())) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_GOAL_ALREADY_COMPLETED);
        }
    }


    private void validateFactors(List<String> factorKeys, Boolean isSuccess) {
        if (CollectionUtils.isEmpty(factorKeys) || factorKeys.size() > 3) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_FACTORS_SIZE);
        }

        boolean allKeysValid;
        if (isSuccess){
            allKeysValid = factorKeys.stream().allMatch(SuccessFactor::isValidKey);
        } else {
            allKeysValid = factorKeys.stream().allMatch(FailureFactor::isValidKey);
        }

        if (!allKeysValid) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_INVALID_FACTOR_KEY);
        }
    }
}