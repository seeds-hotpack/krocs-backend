package com.hotpack.krocs.domain.stopwatch.repository;

import com.hotpack.krocs.domain.goals.domain.SubGoal;
import com.hotpack.krocs.domain.stopwatch.domain.StopwatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StopwatchLogRepository extends JpaRepository<StopwatchLog, Long> {

    List<StopwatchLog> findAllBySubGoal(SubGoal subGoal);
}
