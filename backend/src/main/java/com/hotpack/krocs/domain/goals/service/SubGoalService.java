package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalUpdateResponseDTO;

public interface SubGoalService {


    SubGoalCreateResponseDTO createSubGoals(Long userId, Long goalId,
        SubGoalCreateRequestDTO requestDTO);

    SubGoalListResponseDTO getAllSubGoals(Long userId, Long goalId);

    SubGoalUpdateResponseDTO updateSubGoal(Long userId, Long goalId, Long subGoalId,
        SubGoalUpdateRequestDTO requestDTO);

    void deleteSubGoal(Long userId, Long goalId, Long subGoalId);
}
