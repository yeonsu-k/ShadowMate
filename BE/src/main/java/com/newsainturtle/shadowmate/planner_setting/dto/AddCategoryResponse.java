package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddCategoryResponse {

    private Long categoryId;

    @Builder
    public AddCategoryResponse(Long categoryId) {
        this.categoryId = categoryId;
    }
}
