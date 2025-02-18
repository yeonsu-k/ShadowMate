package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TimeTableRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyPlannerServiceImpl extends DateCommonService implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TimeTableRepository timeTableRepository;
    private final UserRepository userRepository;
    private final SocialRepository socialRepository;

    private DailyPlanner getOrCreateDailyPlanner(final User user, final String date) {
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(date)
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    private DailyPlanner getDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    private Category getCategory(final User user, final Long categoryId) {
        if (categoryId == 0) return null;
        Category category = categoryRepository.findByUserAndId(user, categoryId);
        if (category == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_CATEGORY);
        }
        return category;
    }

    private Todo getTodo(final Long todoId, final DailyPlanner dailyPlanner) {
        final Todo todo = todoRepository.findByIdAndDailyPlanner(todoId, dailyPlanner);
        if (todo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        return todo;
    }

    private DailyPlanner getAnotherUserDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        if (user.getId().equals(plannerWriterId)) {
            throw new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
        }

        final User plannerWriter = certifyPlannerWriter(plannerWriterId);

        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, date);
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    private void checkValidDateTime(final String date, final LocalDateTime startTime, final LocalDateTime endTime) {
        LocalDateTime localDateTime = stringToLocalDateTime(date + " 00:00");
        if (endTime.isBefore(startTime)
                || localDateTime.withHour(4).isAfter(startTime)
                || localDateTime.plusDays(1).withHour(4).isBefore(endTime)) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME);
        }
    }

    private User certifyPlannerWriter(final long plannerWriterId) {
        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }
        return plannerWriter;
    }

    @Override
    public AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, addDailyTodoRequest.getDate());
        final Category category = getCategory(user, addDailyTodoRequest.getCategoryId());
        final Todo todo = Todo.builder()
                .category(category)
                .todoContent(addDailyTodoRequest.getTodoContent())
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build();
        final Todo saveTodo = todoRepository.save(todo);
        return AddDailyTodoResponse.builder().todoId(saveTodo.getId()).build();
    }

    @Override
    public void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest) {
        final TodoStatus status = TodoStatus.parsing(updateDailyTodoRequest.getTodoStatus());
        if (status == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS);
        }
        final DailyPlanner dailyPlanner = getDailyPlanner(user, updateDailyTodoRequest.getDate());
        final Category category = getCategory(user, updateDailyTodoRequest.getCategoryId());
        final Todo todo = getTodo(updateDailyTodoRequest.getTodoId(), dailyPlanner);
        todo.updateTodoContentAndCategoryAndStatus(updateDailyTodoRequest.getTodoContent(), category, status);
    }

    @Override
    public void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeDailyTodoRequest.getDate());
        final Todo todo = getTodo(removeDailyTodoRequest.getTodoId(), dailyPlanner);
        todoRepository.deleteByIdAndDailyPlanner(todo.getId(), dailyPlanner);
    }

    @Override
    public void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTodayGoalRequest.getDate());
        dailyPlanner.updateTodayGoal(updateTodayGoalRequest.getTodayGoal());
    }

    @Override
    public void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTomorrowGoalRequest.getDate());
        dailyPlanner.updateTomorrowGoal(updateTomorrowGoalRequest.getTomorrowGoal());
    }

    @Override
    public void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionRequest.getDate());
        dailyPlanner.updateRetrospection(updateRetrospectionRequest.getRetrospection());
    }

    @Override
    public void updateRetrospectionImage(final User user, final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionImageRequest.getDate());
        dailyPlanner.updateRetrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage());
    }

    @Override
    public void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId, addDailyPlannerLikeRequest.getDate());
        DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user, dailyPlanner);
        if (dailyPlannerLike != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE);
        }
        dailyPlannerLike = DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user)
                .build();
        dailyPlannerLikeRepository.save(dailyPlannerLike);
    }

    @Override
    public void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId, removeDailyLikeRequest.getDate());
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user, dailyPlanner);
    }

    @Override
    public void addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest) {
        LocalDateTime startTime = stringToLocalDateTime(addTimeTableRequest.getStartTime());
        LocalDateTime endTime = stringToLocalDateTime(addTimeTableRequest.getEndTime());
        checkValidDateTime(addTimeTableRequest.getDate(), startTime, endTime);

        final DailyPlanner dailyPlanner = getDailyPlanner(user, addTimeTableRequest.getDate());
        final Todo todo = getTodo(addTimeTableRequest.getTodoId(), dailyPlanner);

        if (todo.getTimeTable() != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_TIME_TABLE);
        }

        todo.setTimeTable(TimeTable.builder()
                .endTime(endTime)
                .startTime(startTime)
                .build());
    }

    @Override
    public void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeTimeTableRequest.getDate());
        final Todo todo = getTodo(removeTimeTableRequest.getTodoId(), dailyPlanner);
        if (todo.getTimeTable() == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME_TABLE);
        }
        final long timeTableId = todo.getTimeTable().getId();
        todo.setTimeTable(null);
        timeTableRepository.deleteById(timeTableId);
    }

    @Override
    public ShareSocialResponse shareSocial(final User user, final ShareSocialRequest shareSocialRequest) {
        if (!user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC)) {
            throw new PlannerException(PlannerErrorResult.FAILED_SHARE_SOCIAL);
        }
        final DailyPlanner dailyPlanner = getDailyPlanner(user, shareSocialRequest.getDate());
        final Social findSocial = socialRepository.findByDailyPlanner(dailyPlanner);
        if (findSocial != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_SHARED_SOCIAL);
        }
        final Social social = Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(shareSocialRequest.getSocialImage())
                .build();
        final Social saveSocial = socialRepository.save(social);
        return ShareSocialResponse.builder().socialId(saveSocial.getId()).build();
    }
}
