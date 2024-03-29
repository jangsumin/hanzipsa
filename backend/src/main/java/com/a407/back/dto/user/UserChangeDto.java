package com.a407.back.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserChangeDto {

    private String profileImage;
    private String address;
    private Double latitude;
    private Double longitude;
    private String password;

}
