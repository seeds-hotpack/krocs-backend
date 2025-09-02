package com.hotpack.krocs.domain.goals.service;

import com.hotpack.krocs.domain.goals.dto.request.GoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.GoalUpdateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.request.SubGoalCreateRequestDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.GoalResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalCreateResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalListResponseDTO;
import com.hotpack.krocs.domain.goals.dto.response.SubGoalResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface GoalService {

    GoalCreateResponseDTO createGoal(GoalCreateRequestDTO requestDTO, Long userId);

    List<GoalResponseDTO> getGoalByUser(Long userId, LocalDate dateTime);

    GoalResponseDTO getGoalByGoalId(Long userId, Long goalId);

    GoalResponseDTO updateGoalById(Long goalId, GoalUpdateRequestDTO request, Long userId);

    void deleteGoal(Long userId, Long goalId);

    SubGoalCreateResponseDTO createSubGoals(Long goalId, SubGoalCreateRequestDTO requestDTO);

    SubGoalListResponseDTO getAllSubGoals(Long goalId);

    SubGoalResponseDTO getSubGoal(Long goalId, Long subGoalId);
}