package com.a407.back.model.service;

import static java.security.SecureRandom.getInstanceStrong;

import com.a407.back.config.constants.ErrorCode;
import com.a407.back.domain.Board;
import com.a407.back.domain.BoardTag;
import com.a407.back.domain.Complain;
import com.a407.back.domain.Notification;
import com.a407.back.domain.Room;
import com.a407.back.domain.Room.Process;
import com.a407.back.domain.User;
import com.a407.back.domain.Zipsa;
import com.a407.back.dto.board.BoardListResponse;
import com.a407.back.dto.notification.NotificationListResponse;
import com.a407.back.dto.room.UserPublicRoomListResponse;
import com.a407.back.dto.user.UserAccountRequest;
import com.a407.back.dto.user.UserAccountResponse;
import com.a407.back.dto.user.UserChangeDto;
import com.a407.back.dto.user.UserChangeRequest;
import com.a407.back.dto.user.UserComplainRequest;
import com.a407.back.dto.user.UserCreateRequest;
import com.a407.back.dto.user.UserDetailInfoResponse;
import com.a407.back.dto.user.UserEmailDuplicationRequest;
import com.a407.back.dto.user.UserInfoResponse;
import com.a407.back.dto.user.UserLocationResponse;
import com.a407.back.dto.user.UserNearZipsaInfoResponse;
import com.a407.back.dto.user.UserNearZipsaLocationResponse;
import com.a407.back.dto.user.UserNearZipsaRequest;
import com.a407.back.dto.user.UserPhoneNumberAndEmail;
import com.a407.back.dto.user.UserRecordInfoResponse;
import com.a407.back.dto.user.UserRecordResponse;
import com.a407.back.dto.user.UserReservationInfoResponse;
import com.a407.back.dto.user.UserReservationResponse;
import com.a407.back.dto.util.BoardListDto;
import com.a407.back.config.ImageConfig;
import com.a407.back.dto.util.UserPublicRoom;
import com.a407.back.exception.CustomException;
import com.a407.back.model.repo.BoardRepository;
import com.a407.back.model.repo.CategoryRepository;
import com.a407.back.model.repo.CommentRepository;
import com.a407.back.model.repo.ComplainRepository;
import com.a407.back.model.repo.RoomRepository;
import com.a407.back.model.repo.UserRepository;
import com.a407.back.model.repo.ZipsaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ZipsaRepository zipsaRepository;

    private final CategoryRepository categoryRepository;

    private final DefaultMessageService messageService;

    private final RoomRepository roomRepository;

    private final ComplainRepository complainRepository;

    private final BoardRepository boardRepository;

    private final CommentRepository commentRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ImageConfig imageConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sms.number}")
    private String SENDER_PHONE_NUMBER;

    @Value("${image.size.profile}")
    private Integer PROFILE_SIZE;

    @Value("${code.phone.start}")
    private Integer CODE_START;

    @Value("${code.phone.end}")
    private Integer CODE_END;

    @Override
    public UserInfoResponse findUserInfo(Long userId) {

        User user = userRepository.findByUserId(userId);
        Zipsa zipsa = zipsaRepository.findByZipsaId(userId);

        return new UserInfoResponse(user.getName(), user.getProfileImage(), user.getIsAffiliated(),
            zipsa != null);
    }

    @Override
    public UserLocationResponse findUserLocation(Long userId) {
        User user = userRepository.findByUserId(userId);
        return new UserLocationResponse(user.getLatitude(), user.getLongitude());
    }

    @Override
    public Boolean findEmailDuplication(UserEmailDuplicationRequest userEmailDuplicationRequest) {
        User user = userRepository.findByUserEmail(userEmailDuplicationRequest.getEmail());
        return user == null;
    }

    @Override
    @Transactional
    public Long makeUser(UserCreateRequest request) {
        // 에러 처리
        if (userRepository.findByUserEmail(request.getEmail()) != null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(bCryptPasswordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .birth(request.getBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .isAdmin(false)
            .isCertificated(false)
            .isBlocked(false)
            .isAffiliated(false)
            .build();

        return userRepository.makeUser(user).getUserId();
    }

    @Override
    @Transactional
    public List<NotificationListResponse> findNotificationByUserIdList(Long userId) {
        boolean workedDistinction = isWorkedDistinction(userId);
        List<Notification> notificationList = null;
        List<NotificationListResponse> notificationResponseList = new ArrayList<>();
        if (workedDistinction) {
            notificationList = userRepository.findNotificationByUserIdList(userId, "ZIPSA");
        } else {
            notificationList = userRepository.findNotificationByUserIdList(userId, "USER");
        }

        for (Notification n : notificationList) {
            Room room = roomRepository.findByRoomId(n.getRoomId().getRoomId());
            notificationResponseList.add(
                new NotificationListResponse(userRepository.findByUserId(n.getSendId()).getName(),
                    n.getType(), room.getStatus(), n.getStatus(),
                    categoryRepository.findMajorCategoryName(
                        n.getRoomId().getSubCategoryId().getMajorCategoryId().getMajorCategoryId()),
                    n.getRoomId().getRoomId(), n.getNotificationId(), n.getCreatedAt()));
        }
        return notificationResponseList;
    }

    @Override
    public boolean isWorkedDistinction(Long userId) {
        Zipsa zipsa = zipsaRepository.findByZipsaId(userId);
        return zipsa != null && zipsa.getIsWorked();
    }


    @Override
    public List<UserNearZipsaLocationResponse> findNearZipsaLocationList(Long userId) {
        return userRepository.findNearZipsaLocationList(userId).stream().map(
            zipsa -> new UserNearZipsaLocationResponse(zipsa.getZipsaId().getLatitude(),
                zipsa.getZipsaId().getLongitude())).toList();
    }

    @Override
    public List<UserNearZipsaInfoResponse> findNearZipsaInfoList(
        UserNearZipsaRequest userNearZipsaRequest) {
        return userRepository.findNearZipsaInfoList(userNearZipsaRequest.getLat(),
                userNearZipsaRequest.getLng()).stream().map(
                zipsa -> new UserNearZipsaInfoResponse(zipsa.getZipsaId().getName(),
                    zipsa.getZipsaId().getGender(), zipsa.getGradeId().getName(),
                    zipsa.getDescription(), zipsa.getPreferTag(), zipsa.getZipsaId().getUserId()))
            .toList();
    }

    @Override
    public User findByUserId(Long userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public UserRecordInfoResponse getUserRecordInfo(Long roomId) {
        Room room = userRepository.getUserRecordInfo(roomId);
        return UserRecordInfoResponse.builder().roomId(room.getRoomId())
            .zipsaId(room.getZipsaId().getZipsaId().getUserId())
            .name(room.getZipsaId().getZipsaId().getName()).profile(
                room.getZipsaId().getZipsaId().getProfileImage())
            .subCategoryName(room.getSubCategoryId().getName())
            .majorCategoryName(room.getSubCategoryId().getMajorCategoryId().getName())
            .content(room.getContent()).estimateDuration(room.getEstimateDuration())
            .roomCreatedAt(room.getRoomCreatedAt()).matchCreatedAt(room.getMatchCreatedAt())
            .isReported(room.getIsReported()).reportCycle(room.getReportCycle())
            .isPublic(room.getIsPublic()).startedAt(room.getStartedAt())
            .endedAt(room.getEndedAt()).expectationStartedAt(room.getExpectationStartedAt())
            .expectationEndedAt(room.getExpectationEndedAt())
            .expectationPay(room.getExpectationPay()).totalPay(room.getTotalPay())
            .isComplained(room.getIsComplained()).isReviewed(room.getIsReviewed()).build();
    }

    @Override
    public List<UserRecordResponse> getUserRecordList(Long userId) {
        return userRepository.getUserRecordList(userId, false).stream().map(
            room -> new UserRecordResponse(room.getRoomId(),
                room.getZipsaId().getZipsaId().getName(),
                room.getSubCategoryId().getName(), room.getEndedAt(),
                room.getTotalPay())).toList();
    }

    @Override
    public UserReservationInfoResponse getUserReservationInfo(Long roomId) {
        Room room = userRepository.getUserReservationInfo(roomId);
        return UserReservationInfoResponse.builder()
            .zipsaId(room.getZipsaId().getZipsaId().getUserId())
            .name(room.getZipsaId().getZipsaId().getName())
            .profile(room.getZipsaId().getZipsaId().getProfileImage())
            .subCategoryName(room.getSubCategoryId().getName())
            .majorCategoryName(room.getSubCategoryId().getMajorCategoryId().getName())
            .content(room.getContent()).estimateDuration(room.getEstimateDuration())
            .roomCreatedAt(room.getRoomCreatedAt()).matchCreatedAt(room.getMatchCreatedAt())
            .isReported(room.getIsReported()).reportCycle(room.getReportCycle())
            .isPublic(room.getIsPublic()).startedAt(room.getStartedAt())
            .endedAt(room.getEndedAt()).expectationStartedAt(room.getExpectationStartedAt())
            .expectationEndedAt(room.getExpectationEndedAt())
            .expectationPay(room.getExpectationPay()).status(room.getStatus()).build();
    }

    @Override
    public List<UserReservationResponse> getUserReservationList(Long userId) {
        boolean isZipsa = isWorkedDistinction(userId);
        return userRepository.getUserReservationList(userId, isZipsa).stream().map(
            room -> new UserReservationResponse(room.getRoomId(),
                isZipsa ? room.getUserId().getName() : room.getZipsaId().getZipsaId().getName(),
                room.getSubCategoryId().getMajorCategoryId().getName(), room.getStatus(),
                room.getExpectationStartedAt())).toList();
    }

    @Override
    public UserReservationResponse getUserReservationFirst(Long userId) {
        boolean isZipsa = isWorkedDistinction(userId);
        Room room = userRepository.getUserReservationOngoing(userId, isZipsa);
        if (room == null) {
            room = userRepository.getUserReservationBefore(userId, isZipsa);
        }
        if (room == null) {
            return null;
        }
        return new UserReservationResponse(room.getRoomId(),
            isZipsa ? room.getUserId().getName() :
                room.getZipsaId().getZipsaId().getName(),
            room.getSubCategoryId().getMajorCategoryId().getName(), room.getStatus(),
            room.getExpectationStartedAt());
    }

    @Override
    @Transactional
    public UserAccountResponse makeAccount(Long userId, UserAccountRequest userAccountRequest) {
        User user = userRepository.findByUserId(userId);
        if (user.getAccount() != null && user.getAccount()
            .equals(userAccountRequest.getAccount())) {
            return new UserAccountResponse("이미 등록된 카드입니다");
        }

        userRepository.makeAccount(user.getUserId(), userAccountRequest.getAccount());

        return new UserAccountResponse("카드 등록에 성공하였습니다.");
    }

    @Override
    public String getMaskedCardNumber(Long userId) {
        User user = userRepository.findByUserId(userId);
        String cardNumber = user.getAccount();
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    @Override
    public void makeSendMessage(String phoneNumber, String email)
        throws JsonProcessingException, NoSuchAlgorithmException {
        int code = getInstanceStrong().nextInt(CODE_START,
            CODE_END);
        while (userRepository.findCode(String.valueOf(code)) != null) {
            code = getInstanceStrong().nextInt(CODE_START,
                CODE_END);
        }
        Message message = new Message();
        message.setFrom(SENDER_PHONE_NUMBER);
        message.setTo(phoneNumber);
        message.setText("인증 번호 " + code + " 를 입력해주세요");
        SingleMessageSentResponse response = messageService.sendOne(
            new SingleMessageSendingRequest(message));
        if (response == null || !response.getStatusCode().equals("2000")) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }
        UserPhoneNumberAndEmail userPhoneNumberAndEmail = new UserPhoneNumberAndEmail(email,
            phoneNumber);
        userRepository.makeSendMessage(userPhoneNumberAndEmail, String.valueOf(code));
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findByUserId(userId);
        userRepository.deleteAccount(user, user.getAccount());
    }

    @Override
    @Transactional
    public void makePhoneNumber(String code, String email) throws JsonProcessingException {
        UserPhoneNumberAndEmail userPhoneNumberAndEmail = userRepository.findMessage(code);
        if (userPhoneNumberAndEmail == null || !userPhoneNumberAndEmail.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        userRepository.makePhoneNumber(userPhoneNumberAndEmail.getPhoneNumber(),
            userPhoneNumberAndEmail.getEmail());
    }

    @Override
    @Transactional
    public void changeUserCertificated(Long userId) {
        userRepository.changeUserCertificated(userId);
    }

    @Override
    @Transactional
    public void makeComplain(UserComplainRequest userComplainRequest) {
        Room room = roomRepository.findByRoomId(userComplainRequest.getRoomId());
        if (room == null || room.getIsComplained() || room.getStatus() != Process.END
            || complainRepository.findComplain(userComplainRequest.getRoomId()) != null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        Complain complain = Complain.builder()
            .roomId(roomRepository.findByRoomId(userComplainRequest.getRoomId()))
            .content(userComplainRequest.getContent()).isProcessed(false).build();
        complainRepository.makeComplain(complain);
        roomRepository.changeIsComplained(room.getRoomId());
    }

    @Override
    public UserPublicRoomListResponse getUserPublicRoomList(Long userId) {
        User user = userRepository.findByUserId(userId);
        List<UserPublicRoom> userPublicRoomList = roomRepository.getUserPublicRoomList(user)
            .stream()
            .map(
                room -> new UserPublicRoom(room.getRoomId(), room.getTitle(),
                    room.getRoomCreatedAt())).toList();
        return new UserPublicRoomListResponse((long) userPublicRoomList.size(), 1,
            userPublicRoomList);
    }

    @Override
    public BoardListResponse getUserBoardList(Long userId, int page, int size) {
        User user = userRepository.findByUserId(userId);
        List<Board> boardList = boardRepository.getUserBoardList(user, (page - 1) * size,
            size);
        List<BoardListDto> userBoardList = boardList.stream().map(board -> {
            int commentCount = commentRepository.getCommentCount(board).intValue();
            List<BoardTag> tagList = boardRepository.findBoardTagList(board);
            List<String> tagNameList = tagList.stream()
                .map(tag -> tag.getBoardTagId().tagId.getName()).toList();
            return new BoardListDto(board.getBoardId(), board.getTitle(),
                board.getUserId().getName(), commentCount,
                board.getUpdatedAt(), tagNameList);
        }).toList();
        return new BoardListResponse(userBoardList.size(), page, userBoardList);
    }

    @Override
    @Transactional
    public void changeUserInfo(Long userId, UserChangeRequest request, MultipartFile image)
        throws IOException {
        User user = userRepository.findByUserId(userId);
        String imageName = null;
        if (image != null) {
            if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
                imageConfig.deleteImage(user.getProfileImage());
            }
            imageName = imageConfig.resizeImage(image, PROFILE_SIZE);
        }
        UserChangeDto userChangeDto = new UserChangeDto(
            imageName == null ? user.getProfileImage() : imageName,
            request.getAddress() == null ? user.getAddress() : request.getAddress(),
            request.getLatitude() == null ? user.getLatitude() : request.getLatitude(),
            request.getLongitude() == null ? user.getLongitude() : request.getLongitude(),
            request.getPassword() == null ? user.getPassword()
                : bCryptPasswordEncoder.encode(request.getPassword()));
        userRepository.changeUserInfo(userId, userChangeDto);
    }

    @Override
    @Transactional
    public UserDetailInfoResponse findUserDetailInfo(Long userId) {
        User user = userRepository.findByUserId(userId);
        return UserDetailInfoResponse.builder().profileImage(user.getProfileImage())
            .name(user.getName())
            .birth(user.getBirth()).email(user.getEmail()).phoneNumber(user.getPhoneNumber())
            .address(user.getAddress())
            .build();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        zipsaRepository.deleteZipsa(userId);
        userRepository.deleteUser(userId);
    }

}
