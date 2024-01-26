package com.a407.back.model.service;

import com.a407.back.dto.Review.ReviewCreateRequest;
import com.a407.back.dto.Review.ReviewListResponse;
import com.a407.back.dto.util.ApiResponse;
import java.util.List;

public interface ReviewService {

    void createReview(ReviewCreateRequest reviewCreateRequest);

    ApiResponse<List<ReviewListResponse>> getReviewsByUserId(Long userId);
}