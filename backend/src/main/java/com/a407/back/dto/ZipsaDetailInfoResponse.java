package com.a407.back.dto;

import com.a407.back.domain.Review;
import com.a407.back.domain.User.Gender;
import com.a407.back.domain.Zipsa;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ZipsaDetailInfoResponse {

    private final String name;

    private final Timestamp birth;

    private final Gender gender;

    private final String address;

    private final String profileImage;

    private final Double latitude;

    private final Double longitude;

    private final Long gradeId;

    private final String gradeName;

    private final int salary;

    private final String description;

    private final String preferTag;

    private final int serviceCount;

    private final Double replyAverage;

    private final int replyCount;

    private final Double kindnessAverage;

    private final Double skillAverage;

    private final Double rewindAverage;

    private final List<ZipsaReview> reviews;


    public ZipsaDetailInfoResponse(Zipsa zipsa, List<Review> reviewList) {
        this.name = zipsa.getZipsaId().getName();
        this.birth = zipsa.getZipsaId().getBirth();
        this.gender = zipsa.getZipsaId().getGender();
        this.address = zipsa.getZipsaId().getAddress();
        if (zipsa.getZipsaId().getProfileImage() == null) {
            this.profileImage = null;
        } else {
            this.profileImage = new String(zipsa.getZipsaId().getProfileImage());
        }
        this.latitude = zipsa.getZipsaId().getLatitude();
        this.longitude = zipsa.getZipsaId().getLongitude();
        this.gradeId = zipsa.getGradeId().getGradeId();
        this.gradeName = zipsa.getGradeId().getName();
        this.salary = zipsa.getGradeId().getSalary();
        this.description = zipsa.getDescription();
        this.preferTag = zipsa.getPreferTag();
        this.serviceCount = zipsa.getServiceCount();
        this.replyAverage = zipsa.getReplyAverage();
        this.replyCount = zipsa.getReplyCount();
        this.kindnessAverage = zipsa.getKindnessAverage();
        this.skillAverage = zipsa.getSkillAverage();
        this.rewindAverage = zipsa.getRewindAverage();
        this.reviews = new ArrayList<>();
        for (Review review : reviewList) {
            this.reviews.add(new ZipsaReview(review));
        }

    }


}
