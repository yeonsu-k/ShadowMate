package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.user.entity.User;

public interface DailyPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
    void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest);
    void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest);
    void addDailyLike(final User user, final AddDailyLikeRequest addDailyPlannerLikeRequest);
}
