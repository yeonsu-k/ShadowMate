package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.social.dto.SearchNicknamePublicDailyPlannerRequest;
import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
import com.newsainturtle.shadowmate.social.dto.SearchSocialResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.newsainturtle.shadowmate.social.exception.SocialErrorResult.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialServiceImpl implements SocialService {

    private final SocialRepository socialRepository;

    private final UserRepository userRepository;

    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;

    class SocialLike implements Comparable<SocialLike> {
        Social social;
        long cnt;

        public SocialLike(Social social, long cnt) {
            this.social = social;
            this.cnt = cnt;
        }

        @Override
        public int compareTo(SocialLike o) {
            return (int)(o.cnt - this.cnt);
        }
    }

    @Override
    public SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final long pageNumber) {
        List<Social> socialList = socialRepository.findAllByDeleteTime();
        return makeSearchPlannerResponse(socialList, sort, pageNumber);
    }

    @Override
    public SearchPublicDailyPlannerResponse searchNicknamePublicDailyPlanner(final SearchNicknamePublicDailyPlannerRequest searchNicknamePublicDailyPlannerRequest) {
        User user = userRepository.findByNicknameAndPlannerAccessScope(searchNicknamePublicDailyPlannerRequest.getNickname(), PlannerAccessScope.PUBLIC);
        List<Social> socialList = new ArrayList<>();
        if(user!=null) {
            socialList = socialRepository.findAllByDailyPlannerAndSocial(user.getId());
        }
        return makeSearchPlannerResponse(socialList, searchNicknamePublicDailyPlannerRequest.getSort(), searchNicknamePublicDailyPlannerRequest.getPageNumber());
    }

    @Override
    @Transactional
    public void deleteSocial(final long socialId) {
        Optional<Social> social = socialRepository.findById(socialId);
        if(social.isPresent()) {
            socialRepository.delete(social.get());
        }
        else {
            throw new SocialException(NOT_FOUND_SOCIAL);
        }
    }

    private SearchPublicDailyPlannerResponse makeSearchPlannerResponse(List<Social> socialList, String sort, long pageNumber) {
        if(socialList.isEmpty()) {
            return SearchPublicDailyPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(1L)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }
        int totalPage = socialList.size()/6;
        if(0 < socialList.size() % 6) totalPage++;
        if(totalPage < pageNumber) throw new SocialException(NOT_FOUND_PAGE_NUMBER);
        if(sort.equals("latest")) {
            socialList.sort(new Comparator<Social>() {
                @Override
                public int compare(Social o1, Social o2) {
                    if(o2.getDailyPlanner().getDailyPlannerDay().equals(o1.getDailyPlanner().getDailyPlannerDay())) {
                        return o2.getId().compareTo(o1.getId());
                    }
                    return o2.getDailyPlanner().getDailyPlannerDay().compareTo(o1.getDailyPlanner().getDailyPlannerDay());
                }
            });
            List<Social> newList = new ArrayList<>();
            for(int i=(int)(pageNumber-1)*6, cnt=0; i<socialList.size(); i++, cnt++) {
                if(cnt==6) break;
                newList.add(socialList.get(i));
            }
            return SearchPublicDailyPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(makeSearchSocialResponseList(newList))
                    .build();
        }
        else if(sort.equals("popularity")) {
            List<SocialLike> socialLikeList = socialList.stream()
                    .map(social -> new SocialLike(social,dailyPlannerLikeRepository.countByDailyPlanner(social.getDailyPlanner())))
                    .collect(Collectors.toList());
            Collections.sort(socialLikeList);
            List<Social> newList = new ArrayList<>();
            for(int i=(int)(pageNumber-1)*6, cnt=0; i<socialList.size(); i++, cnt++) {
                if(cnt==6) break;
                newList.add(socialLikeList.get(i).social);
            }
            return SearchPublicDailyPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(makeSearchSocialResponseList(newList))
                    .build();
        }
        else {
            throw new SocialException(BAD_REQUEST_SORT);
        }
    }

    private List<SearchSocialResponse> makeSearchSocialResponseList(List<Social> socialList) {
        return socialList.stream()
                .map(social -> SearchSocialResponse.builder()
                        .socialId(social.getId())
                        .socialImage(social.getSocialImage())
                        .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                        .userId(social.getDailyPlanner().getUser().getId())
                        .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                        .nickname(social.getDailyPlanner().getUser().getNickname())
                        .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                        .build())
                .collect(Collectors.toList());
    }
}
