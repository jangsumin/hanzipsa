package com.a407.back.model.service;

import com.a407.back.config.constants.ErrorCode;
import com.a407.back.domain.Review;
import com.a407.back.domain.Room;
import com.a407.back.domain.Zipsa;
import com.a407.back.dto.review.ReviewCreateRequest;
import com.a407.back.dto.review.ReviewListResponse;
import com.a407.back.exception.CustomException;
import com.a407.back.model.repo.ReviewRepository;
import com.a407.back.model.repo.RoomRepository;
import com.a407.back.model.repo.ZipsaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final RoomRepository roomRepository;

    private final ZipsaRepository zipsaRepository;

    @Override
    @Transactional
    public void makeReview(ReviewCreateRequest reviewCreateRequest) {
        Room room = roomRepository.findByRoomId(reviewCreateRequest.getRoomId());
        if (reviewCreateRequest.getRoomId() == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        Review review = reviewCreateRequest.toEntity(room);

        reviewRepository.makeReview(review);
        roomRepository.changeRoomReview(room.getRoomId());

        Zipsa zipsa = zipsaRepository.findByZipsaId(room.getZipsaId().getZipsaId().getUserId());

        Double kindnessAverage = zipsa.getKindnessAverage();
        Double skillAverage = zipsa.getSkillAverage();
        Double rewindAverage = zipsa.getRewindAverage();
        Long countReview = reviewRepository.findCountByZipsaId(zipsa.getZipsaId().getUserId());

        if (countReview == 0) {
            kindnessAverage = (double) reviewCreateRequest.getKindnessScore();
            skillAverage = (double) reviewCreateRequest.getSkillScore();
            rewindAverage = (double) reviewCreateRequest.getRewindScore();
        } else {
            kindnessAverage = getAverageSave(reviewCreateRequest.getKindnessScore(),
                kindnessAverage, countReview);
            skillAverage = getAverageSave(reviewCreateRequest.getSkillScore(), skillAverage,
                countReview);
            rewindAverage = getAverageSave(reviewCreateRequest.getRewindScore(), rewindAverage,
                countReview);
        }

        zipsaRepository.updateZipsaAverage(zipsa.getZipsaId().getUserId(), kindnessAverage,
            skillAverage, rewindAverage);
    }

    @Override
    public List<ReviewListResponse> findReviewsByUserId(Long userId) {
        return reviewRepository.findReviewsByUserId(userId).stream()
            .map(review -> ReviewListResponse.builder()
                .zipsaId(review.getZipsaId().getZipsaId().getUserId())
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .kindnessScore(review.getKindnessScore())
                .skillScore(review.getSkillScore())
                .rewindScore(review.getRewindScore())
                .createdAt(review.getCreatedAt())
                .build()).toList();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {

        Zipsa zipsa = reviewRepository.findZipsaByReviewId(reviewId);
        Review review = reviewRepository.findReviewByReviewId(reviewId);

        Double kindnessAverage = zipsa.getKindnessAverage();
        Double skillAverage = zipsa.getSkillAverage();
        Double rewindAverage = zipsa.getRewindAverage();
        Long countReview = reviewRepository.findCountByZipsaId(zipsa.getZipsaId().getUserId());

        if (countReview == 1) {
            kindnessAverage = 0D;
            skillAverage = 0D;
            rewindAverage = 0D;
        } else {
            kindnessAverage = getAverageRemove(review.getKindnessScore(), kindnessAverage,
                countReview);
            skillAverage = getAverageRemove(review.getSkillScore(), skillAverage, countReview);
            rewindAverage = getAverageRemove(review.getRewindScore(), rewindAverage, countReview);
        }
        zipsaRepository.updateZipsaAverage(zipsa.getZipsaId().getUserId(), kindnessAverage,
            skillAverage, rewindAverage);

        reviewRepository.deleteReview(reviewId);
    }

    private static double getAverageSave(int score, Double average, Long countReview) {
        return ((average * (countReview-1)) + score) / countReview;
    }

    private static double getAverageRemove(int score, Double average, Long countReview) {
        return ((average * (countReview)) - score) / (countReview - 1);
    }

}
