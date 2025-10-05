package com.hotpack.krocs.domain.retrospectives.service;


import com.hotpack.krocs.domain.goals.converter.GoalConverter;
import com.hotpack.krocs.domain.goals.domain.Goal;
import com.hotpack.krocs.domain.goals.facade.GoalRepositoryFacade;
import com.hotpack.krocs.domain.retrospectives.converter.RetrospectiveConverter;
import com.hotpack.krocs.domain.retrospectives.domain.Retrospective;
import com.hotpack.krocs.domain.retrospectives.domain.RetrospectiveOutcome;
import com.hotpack.krocs.domain.retrospectives.dto.request.RetrospectiveCreateRequestDTO;
import com.hotpack.krocs.domain.retrospectives.dto.response.RetrospectiveCreateResponseDTO;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveException;
import com.hotpack.krocs.domain.retrospectives.exception.RetrospectiveExceptionType;
import com.hotpack.krocs.domain.retrospectives.facade.RetrospectiveRepositoryFacade;
import com.hotpack.krocs.domain.retrospectives.validator.RetrospectiveValidator;
import com.hotpack.krocs.domain.user.domain.User;
import com.hotpack.krocs.domain.user.facade.UserRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrospectiveServiceImpl implements RetrospectiveService {

    private final GoalRepositoryFacade goalRepositoryFacade;
    private final GoalConverter goalConverter;

    private final RetrospectiveRepositoryFacade retrospectiveRepositoryFacade;
    private final RetrospectiveValidator retrospectiveValidator;
    private final RetrospectiveConverter retrospectiveConverter;
    private final UserRepositoryFacade userRepositoryFacade;

    @Override
    @Transactional
    public RetrospectiveCreateResponseDTO createRetrospective(Long userId, Long goalId,
        RetrospectiveCreateRequestDTO requestDTO) {
        try {
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if (user == null) {
                throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_USER_NOT_FOUND);
            }
            Goal goal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            if (goal == null) {
                throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_GOAL_NOT_FOUND);
            }
            retrospectiveValidator.validateCreate(requestDTO);
            Retrospective retro = retrospectiveConverter.toEntity(requestDTO, user, goal);
            Retrospective saveRetro = retrospectiveRepositoryFacade.saveRetrospective(retro);

            return retrospectiveConverter.toCreateResponseDTO(saveRetro);
        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_CREATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteRetrospective(Long userId, Long goalId, Long retroId) {
        try {
            User user = userRepositoryFacade.findActiveUserByUserId(userId);
            if(user == null){
                throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_USER_NOT_FOUND);
            }
            Goal goal = goalRepositoryFacade.findActiveGoalByUserAndGoalId(user, goalId);
            if (goal == null) {
                throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_GOAL_NOT_FOUND);
            }

            retrospectiveValidator.validateDelete(goal, userId, retroId);

            Retrospective retrospective = retrospectiveRepositoryFacade.findActiveRetrospectiveByUserAndRetrospectiveId(
                user, retroId);
            retrospectiveRepositoryFacade.delete(retrospective);

        } catch (RetrospectiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrospectiveException(RetrospectiveExceptionType.RETRO_DELETE_FAILED);
        }
    }

}
