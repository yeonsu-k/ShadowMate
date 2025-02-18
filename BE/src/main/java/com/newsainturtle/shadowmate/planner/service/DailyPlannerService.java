package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface DailyPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
    void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest);
    void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest);
    void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest);
    void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest);
    void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest);
    void updateRetrospectionImage(final User user, final UpdateRetrospectionImageRequest updateRetrospectionImageRequest);
    void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest);
    void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest);
    void addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest);
    void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest);
    ShareSocialResponse shareSocial(final User user, final ShareSocialRequest shareSocialRequest);
}
