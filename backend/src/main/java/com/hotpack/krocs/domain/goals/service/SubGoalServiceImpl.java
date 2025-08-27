package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.converter.SubGoalConverter;
import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;
import com.hotpack.krocs.domain.goals.exception.SubGoalException;
import com.hotpack.krocs.domain.goals.exception.SubGoalExceptionType;
import com.hotpack.krocs.domain.goals.facade.SubGoalRepositoryFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubGoalServiceImpl implements SubGoalService {

    private final SubGoalRepositoryFacade subGoalRepositoryFacade;
    private final SubGoalConverter subGoalConverter;

    @Override
    @Transactional
    public SubGoalUpdateResponseDTO updateSubGoal(Long subGoalId,
        SubGoalUpdateRequestDTO requestDTO) {
        try {
            validateBusinessRules(requestDTO);
            if (subGoalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
            }

            SubGoal subGoal = subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId);
            subGoal.updateFrom(requestDTO);

            return SubGoalConverter.toSubGoalUpdateResponseDTO(
                subGoalRepositoryFacade.findActiveSubGoalBySubGoalId(subGoalId));
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_UPDATE_FAILED);
        }
    }

    private void validateBusinessRules(SubGoalUpdateRequestDTO requestDTO) {
        if (requestDTO.getTitle() == null) {
            return;
        }

        if (requestDTO.getTitle().length() > 200) {
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_TITLE_TOO_LONG);
        }
    }

    @Override
    @Transactional
    public void deleteSubGoal(Long subGoalId) {
        try {
            if (subGoalId == null) {
                throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_ID_IS_NULL);
            }
            subGoalRepositoryFacade.deleteActiveSubGoalBySubGoalId(subGoalId);
        } catch (SubGoalException e) {
            throw e;
        } catch (Exception e) {
            log.error("소목표 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new SubGoalException(SubGoalExceptionType.SUB_GOAL_DELETE_FAILED);
        }
    }

}
