package com.newsainturtle.shadowmate.kh.follow;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.follow.controller.FollowController;
import com.newsainturtle.shadowmate.follow.dto.AddFollowRequest;
import com.newsainturtle.shadowmate.follow.dto.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowerResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FollowControllerTest {

    @InjectMocks
    private FollowController followController;

    @Mock
    private FollowServiceImpl followService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private Gson gson;

    final User user1 = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    final User user2 = User.builder()
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 팔로잉TEST {

        @Nested
        class 팔로잉조회TEST {
            final String url = "/api/follow/{userId}/following";
            final List<FollowingResponse> list = new ArrayList<>();
            final Long userId = 1L;

            @Test
            public void 실패_유저정보다름() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }


            @Test
            public void 실패_팔로잉조회Null() throws Exception {
                //given
                final List<FollowingResponse> followingResponses = new ArrayList<>();
                doReturn(followingResponses).when(followService).getFollowing(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isEmpty());

            }


            @Test
            public void 성공_팔로잉조회() throws Exception {
                //given
                list.add(FollowingResponse.builder()
                        .followId(1L)
                        .email(user2.getEmail())
                        .nickname(user2.getNickname())
                        .profileImage(user2.getProfileImage())
                        .followingId(user2.getId())
                        .build());
                doReturn(list).when(followService).getFollowing(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());

            }
        }
    }
    @Nested
    class 팔로워TEST {
        final String url = "/api/follow/{userId}/followers";
        final List<FollowerResponse> list = new ArrayList<>();
        final Long userId = 2L;

        @Test
        public void 실패_팔로워조회Null() throws Exception {
            //given
            final List<FollowerResponse> followerResponses = new ArrayList<>();
            doReturn(followerResponses).when(followService).getFollower(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
            );

            //then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());

        }

        @Test
        public void 성공_팔로워조회() throws Exception {
            //given
            list.add(FollowerResponse.builder()
                    .followId(1L)
                    .email(user1.getEmail())
                    .nickname(user1.getNickname())
                    .profileImage(user1.getProfileImage())
                    .followerId(user1.getId())
                    .build());
            doReturn(list).when(followService).getFollower(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
            );

            //then
            resultActions.andExpect(status().isOk());

        }
    }

    @Nested
    class 팔로우신청TEST {
        final String url = "/api/follow/{userId}/requested";

        final Long userId = 2L;

        final AddFollowRequest addFollowRequest = AddFollowRequest
                .builder()
                .followingId(userId)
                .build();

        @Test
        public void 실패_중복신청() throws Exception {
            //given
            doThrow(new FollowException(FollowErrorResult.DUPLICATED_FOLLOW)).when(followService).addFollow(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addFollowRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }


        @Test
        public void 실패_팔로우신청_유저없음() throws Exception {
            //given
            doThrow(new FollowException(FollowErrorResult.NOTFOUND_FOLLOWING_USER)).when(followService).addFollow(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addFollowRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isNotFound());
        }


        @Test
        public void 성공_팔로우신청_비공개() throws Exception {
            //given
            final AddFollowResponse addFollowResponse = AddFollowResponse
                    .builder()
                    .followId(1L)
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .build();
            doReturn(addFollowResponse).when(followService).addFollow(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addFollowRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.plannerAccessScope")
                            .value(equalTo(addFollowResponse.getPlannerAccessScope().toString())));

        }

        
        @Test
        public void 성공_팔로우신청_전체공개() throws Exception {
            final AddFollowRequest addFollowRequest = AddFollowRequest
                    .builder()
                    .followingId(userId)
                    .build();
            //given
            final AddFollowResponse addFollowResponse = AddFollowResponse
                    .builder()
                    .followId(1L)
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .build();
            doReturn(addFollowResponse).when(followService).addFollow(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addFollowRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            //then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.plannerAccessScope")
                            .value(equalTo(addFollowResponse.getPlannerAccessScope().toString())));
        }
        
    }
}
