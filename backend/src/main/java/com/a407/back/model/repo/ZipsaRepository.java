package com.a407.back.model.repo;

import com.a407.back.domain.MajorCategory;
import com.a407.back.domain.Report;
import com.a407.back.domain.Review;
import com.a407.back.domain.Room;
import com.a407.back.domain.Zipsa;
import com.a407.back.domain.ZipsaCategory;
import com.a407.back.dto.zipsa.ZipsaChangeDto;
import java.util.List;

public interface ZipsaRepository {

    void makeReport(Report report);

    List<Report> findReportByRoomIdList(Long roomId);

    Zipsa findByZipsaId(Long zipsaId);

    List<String> searchSubCategoryList(Long zipsaId);

    List<Review> searchReviewList(Long zipsaId);

    Room getZipsaRecordInfo(Long roomId);

    Room getZipsaReservationInfo(Long roomId);

    void updateZipsaAverage(Long zipsaId, Double kindnessAverage, Double skillAverage,
        Double rewindAverage);

    void changeServiceCountIncrease(Zipsa zipsa);

    void deleteZipsa(Long zipsaId);

    void changeZipsaInfo(Long zipsaId, ZipsaChangeDto zipsaChangeDto);

    void changeZipsaStatus(Long zipsaId, boolean status);

    Zipsa makeZipsa(Zipsa zipsa);

    void changeZipsaReplyCount(Zipsa zipsa, double replyAverage);

    void makeZipsaCategory(ZipsaCategory zipsaCategory);

    MajorCategory getMajorCategory(Long majorCategoryId);
}