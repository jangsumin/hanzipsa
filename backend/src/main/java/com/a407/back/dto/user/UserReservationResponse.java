package com.a407.back.dto.user;

import com.a407.back.domain.Room.Process;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class UserReservationResponse {

    private Long roomId;
    private String name;
    private String majorCategoryName;
    private Process status;
    private Timestamp expectationStartedAt;

}