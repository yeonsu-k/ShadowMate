package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.UpdateTodayGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateTomorrowGoalRequest;
import com.newsainturtle.shadowmate.user.entity.User;

public interface DailyPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
    void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest);
    void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest);
}
