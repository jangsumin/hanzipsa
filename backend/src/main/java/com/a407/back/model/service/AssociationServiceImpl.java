package com.a407.back.model.service;

import com.a407.back.config.constants.ErrorCode;
import com.a407.back.domain.Association;
import com.a407.back.domain.User;
import com.a407.back.dto.association.AssociationAdditionCodeResponse;
import com.a407.back.dto.user.UserAssociationResponse;
import com.a407.back.exception.CustomException;
import com.a407.back.model.repo.AssociationRepository;
import com.a407.back.model.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssociationServiceImpl implements AssociationService {

    private final AssociationRepository associationRepository;

    private final UserRepository userRepository;

    @Value("${code.association.start}")
    private Integer CODE_START;

    @Value("${code.association.end}")
    private Integer CODE_END;

    @Value("${code.association.time}")
    private Integer CODE_TIME;

    @Override
    @Transactional
    public void makeAssociation(Long userId) {
        Association association = new Association(userId, new Timestamp(new Date().getTime()));

        User user = userRepository.findByUserId(userId);
        if (user == null || Boolean.TRUE.equals(user.getIsAffiliated())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        Long associationId = associationRepository.makeAssociation(association);
        userRepository.makeAssociation(user.getUserId(), associationId);
    }

    @Override
    public List<UserAssociationResponse> getAssociationUserList(Long userId) {
        User user = userRepository.findByUserId(userId);
        Long associationId = user.getAssociationId() != null ? user.getAssociationId().getAssociationId() : 0;
        if(associationId == 0) {
            return new ArrayList<>();
        }
        List<User> users = userRepository.searchAssociationUserList(associationId);
        Long representativeId = associationRepository.findAssociationRepresentative(associationId);
        return users.stream()
            .map(associationUser -> new UserAssociationResponse(associationUser.getUserId(), associationUser.getName(),
                associationUser.getProfileImage(),
                associationUser.getUserId().equals(representativeId))).toList();
    }

    @Override
    @Transactional
    public void deleteAssociation(Long userId) {
        Long associationId = associationRepository.findAssociation(userId);

        if (associationId == null) {
            if (!userRepository.findIsAffiliated(userId)) {
                throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
            }
            userRepository.deleteAssociation(userId);
            return;
        }

        List<Long> userIdList = userRepository.searchAssociationUserIdList(associationId);
        for (Long id : userIdList) {
            userRepository.deleteAssociation(id);
        }
        associationRepository.deleteAssociation(associationId);
    }

    @Override
    @Transactional
    public AssociationAdditionCodeResponse makeAdditionCode(Long userId, String email,
        Long associationId) throws JsonProcessingException, NoSuchAlgorithmException {

        // 현재 사용자가 대표인지 여부 확인
        if (!Objects.equals(associationId, associationRepository.findAssociation(userId))) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        String code = associationRepository.findAdditionCode(email);
        // 이미 코드가 존재한다면 종료
        if (code != null) {
            // 코드가 존재한다면 남은 시간 조회
            return new AssociationAdditionCodeResponse(code,
                associationRepository.findTtl(code).intValue());
        }

        int newCode = SecureRandom.getInstanceStrong()
            .nextInt(CODE_START, CODE_END);
        // 이제 생성한 코드가 중복 체크 및 중복이 아닐 때까지 반복
        while (associationRepository.findAssociationId(String.valueOf(newCode)) != null) {
            newCode = SecureRandom.getInstanceStrong()
                .nextInt(CODE_START, CODE_END);
        }
        // 이제 코드와 연동 계정의 번호 저장
        associationRepository.saveAssociationId(String.valueOf(newCode),
            String.valueOf(associationId));
        // 그리고 대표의 이메일과 코드 저장
        associationRepository.saveCode(email, String.valueOf(newCode));
        return new AssociationAdditionCodeResponse(String.valueOf(newCode),
            CODE_TIME);
    }

    @Override
    @Transactional
    public void changeAssociation(Long userId, String code) {
        User user = userRepository.findByUserId(userId);
        if (Boolean.TRUE.equals(user.getIsAffiliated())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }
        Long associationId = associationRepository.findAssociationId(code);
        if (associationId == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        userRepository.makeAssociation(userId, associationId);
    }

    @Override
    @Transactional
    public void changeAssociationRepresentative(Long representativeId, Long userId) {
        if (Objects.equals(representativeId, userId)) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        User representative = userRepository.findByUserId(representativeId);
        User user = userRepository.findByUserId(userId);

        // 대표 확인 및 사용자 번호가 같은 연동 계정인지 확인
        Long associationId = associationRepository.findAssociation(representativeId);
        if (associationId == null || !Objects.equals(
            representative.getAssociationId().getAssociationId(),
            user.getAssociationId().getAssociationId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        associationRepository.changeAssociationRepresentative(userId, associationId);
    }

    @Override
    public Boolean getAssociationRepresentative(Long userId) {

        User user = userRepository.findByUserId(userId);
        if (user.getAssociationId() == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }

        Association association = associationRepository.getAssociationRepresentative(
            user.getAssociationId().getAssociationId());

        return Objects.equals(association.getUserId(), userId);
    }

}
