package com.a407.back.dto.review;

import com.a407.back.domain.Review;
import com.a407.back.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateRequest {

    private Long roomId;
    private String content;
    private Integer kindnessScore;
    private Integer skillScore;
    private Integer rewindScore;

    public Review toEntity(Room room) {
        return Review.builder().userId(room.getUserId()).zipsaId(room.getZipsaId()).content(content)
            .kindnessScore(kindnessScore).skillScore(skillScore).rewindScore(rewindScore).build();
    }

}
