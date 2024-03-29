package com.a407.back.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.a407.back.BackendApplication;
import com.a407.back.domain.Grade;
import com.a407.back.domain.MajorCategory;
import com.a407.back.domain.Room;
import com.a407.back.domain.SubCategory;
import com.a407.back.domain.User;
import com.a407.back.domain.User.Gender;
import com.a407.back.domain.Zipsa;
import com.a407.back.domain.ZipsaCategory;
import com.a407.back.domain.ZipsaCategoryId;
import com.a407.back.dto.match.MatchSearchRequest;
import com.a407.back.dto.match.MatchSearchResponse;
import com.a407.back.dto.match.RoomCreateRequest;
import com.a407.back.dto.room.MakePublicRoomRequest;
import com.a407.back.dto.user.UserCreateRequest;
import com.a407.back.model.service.MatchService;
import com.a407.back.model.service.RoomService;
import com.a407.back.model.service.UserService;
import com.a407.back.model.service.ZipsaService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = BackendApplication.class)
class MatchControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatchService matchService;

    @Autowired
    UserService userService;

    @Autowired
    ZipsaService zipsaService;

    @Autowired
    RoomService roomService;

    @Autowired
    EntityManager em;

    private MajorCategory majorCategory;

    private Grade grade;

    private User user;

    private SubCategory subCategory;

    @BeforeEach
    void setup() {
        // 사용자 생성
        UserCreateRequest firstUser = new UserCreateRequest("user@abc.com", "user", "user",
            Date.valueOf(LocalDate.of(2024, 1, 1)), Gender.MAN, "서울시", 36.5, 127.5);
        Long userId = userService.makeUser(firstUser);
        user = em.find(User.class, userId);
        // grade 생성
        Grade newGrade = Grade.builder().name("APPRENTICE").salary(3000).build();
        em.persist(newGrade);
        Long gradeId = newGrade.getGradeId();
        grade = em.find(Grade.class, gradeId);
        // 집사 생성
        Zipsa newZipsa = Zipsa.builder().zipsaId(user).account("111").description("Asd")
            .gradeId(grade).isWorked(true).kindnessAverage(1.0).replyAverage(1.0).rewindAverage(1.0)
            .skillAverage(1.0).serviceCount(0).preferTag("abc").replyCount(0).build();
        em.persist(newZipsa);
        Long zipsaId = newZipsa.getZipsaId().getUserId();
        // 집사 가져오기
        Zipsa zipsa = zipsaService.findByZipsaId(zipsaId);
        // 대분류 카테고리 생성
        MajorCategory newMajorCategory = MajorCategory.builder().name("동행").build();
        em.persist(newMajorCategory);
        Long majorCategoryId = newMajorCategory.getMajorCategoryId();
        // 대분류 카테고리 가져오기
        majorCategory = em.find(MajorCategory.class, majorCategoryId);
        // 소분류 카테고리 생성
        SubCategory newSubCategory = SubCategory.builder().majorCategoryId(majorCategory)
            .name("병원 동행").build();
        em.persist(newSubCategory);
        Long subCategoryId = newSubCategory.getSubCategoryId();
        subCategory = em.find(SubCategory.class, subCategoryId);
        // zipsa - category 생성
        ZipsaCategory zipsaCategory = ZipsaCategory.builder().zipsaCategoryId(
            ZipsaCategoryId.builder().majorCategory(majorCategory).zipsa(zipsa).build()).build();
        em.persist(zipsaCategory);
    }

    @Test
    @Transactional
    @DisplayName("필터링 기반 검색 구현")
    void getFilteredZipsaList() {
        // 필터링 기반 탐색
        MatchSearchRequest matchSearchRequest = new MatchSearchRequest(
            majorCategory.getMajorCategoryId(), "MAN", "0", "APPRENTICE", "1");
        List<MatchSearchResponse> matchSearchResponses = matchService.getFilteredZipsaList(
            matchSearchRequest);
        if (!matchSearchResponses.isEmpty()) {
            assertThat(matchSearchResponses.get(0).getName(), is(equalTo("user")));
        } else {
            logger.error("필터링 기반 검색 테스트를 실패했습니다.");
        }
    }

    @Test
    @Transactional
    @DisplayName("필터링 기반 검색 후 방 만들기 구현")
    void makeRoomWithHelper() {
        // 집사1 생성
        UserCreateRequest user1 = new UserCreateRequest("user1@abc.com", "user1", "user1",
            Date.valueOf(LocalDate.of(2024, 1, 1)), Gender.MAN, "서울시", 36.5, 127.5);
        Long userId1 = userService.makeUser(user1);
        User newUser1 = userService.findByUserId(userId1);
        Zipsa newZipsa1 = Zipsa.builder().zipsaId(newUser1).account("111").description("Asd")
            .gradeId(grade).isWorked(true).kindnessAverage(1.0).replyAverage(1.0).rewindAverage(1.0)
            .skillAverage(1.0).serviceCount(0).preferTag("abc").replyCount(0).build();
        em.persist(newZipsa1);
        Long zipsaId1 = newZipsa1.getZipsaId().getUserId();
        Zipsa zipsa1 = zipsaService.findByZipsaId(zipsaId1);
        ZipsaCategory zipsaCategory1 = ZipsaCategory.builder().zipsaCategoryId(
            ZipsaCategoryId.builder().majorCategory(majorCategory).zipsa(zipsa1).build()).build();

        em.persist(zipsaCategory1);

        // 집사2 생성
        UserCreateRequest user2 = new UserCreateRequest("user2@abc.com", "user2", "user2",
            Date.valueOf(LocalDate.of(2024, 1, 1)), Gender.MAN, "서울시", 36.5, 127.5);
        Long userId2 = userService.makeUser(user2);
        User newUser2 = userService.findByUserId(userId2);
        Zipsa newZipsa2 = Zipsa.builder().zipsaId(newUser2).account("111").description("Asd")
            .gradeId(grade).isWorked(true).kindnessAverage(1.0).replyAverage(1.0).rewindAverage(1.0)
            .skillAverage(1.0).serviceCount(0).preferTag("abc").replyCount(0).build();
        em.persist(newZipsa2);
        Long zipsaId2 = newZipsa2.getZipsaId().getUserId();
        Zipsa zipsa2 = zipsaService.findByZipsaId(zipsaId2);
        ZipsaCategory zipsaCategory2 = ZipsaCategory.builder().zipsaCategoryId(
            ZipsaCategoryId.builder().majorCategory(majorCategory).zipsa(zipsa2).build()).build();
        em.persist(zipsaCategory2);

        List<Long> zipsaList = new ArrayList<>();
        zipsaList.add(zipsaId1);
        zipsaList.add(zipsaId2);

        RoomCreateRequest roomCreateRequest = new RoomCreateRequest(user.getUserId(),
            subCategory.getSubCategoryId(), "title", "content", "place", 2,
            Timestamp.valueOf("2024-01-01 01:01:01"), Timestamp.valueOf("2024-01-01 01:01:01"),
            Timestamp.valueOf("2024-01-01 01:01:01"), 15000, zipsaList);
        Long roomId = matchService.makeFilterRoom(user.getUserId(), roomCreateRequest);
        Room room = roomService.findByRoomId(roomId);
        if (room != null) {
            assertThat(room.getUserId().getName(), is(equalTo("user")));
        } else {
            logger.error("필터링 기반 검색 후 방 만들기 테스트를 실패했습니다.");
        }
    }

    @Test
    @Transactional
    @DisplayName("업무 시작 버튼 구현")
    void changeMatchStartedAt() {
        // 집사1 생성
        UserCreateRequest user1 = new UserCreateRequest("user1@abc.com", "user1", "user1",
            Date.valueOf(LocalDate.of(2024, 1, 1)), Gender.MAN, "서울시", 36.5, 127.5);
        Long userId1 = userService.makeUser(user1);
        User newUser1 = userService.findByUserId(userId1);
        Zipsa newZipsa1 = Zipsa.builder().zipsaId(newUser1).account("111").description("Asd")
            .gradeId(grade).isWorked(true).kindnessAverage(1.0).replyAverage(1.0).rewindAverage(1.0)
            .skillAverage(1.0).serviceCount(0).preferTag("abc").replyCount(0).build();
        em.persist(newZipsa1);
        Long zipsaId1 = newZipsa1.getZipsaId().getUserId();
        Zipsa zipsa1 = zipsaService.findByZipsaId(zipsaId1);
        ZipsaCategory zipsaCategory1 = ZipsaCategory.builder().zipsaCategoryId(
            ZipsaCategoryId.builder().majorCategory(majorCategory).zipsa(zipsa1).build()).build();
        em.persist(zipsaCategory1);

        // 방 만들기
        Long roomId = roomService.makePublicRoom(
            new MakePublicRoomRequest(subCategory.getSubCategoryId(), "title",
                "content", "place", 2, Timestamp.valueOf("2024-01-01 01:01:01"),
                Timestamp.valueOf("2024-01-01 01:01:01"), Timestamp.valueOf("2024-01-01 01:01:01"),
                15000), user.getUserId());
        matchService.changeMatchStartedAt(roomId);
        em.flush();
        em.clear();
        Room room = roomService.findByRoomId(roomId);
        assertNotNull(room.getStartedAt());
    }

    @Test
    @Transactional
    @DisplayName("업무 종료 버튼 구현")
    void changeMatchEndedAt() {
        // 집사1 생성
        UserCreateRequest user1 = new UserCreateRequest("user1@abc.com", "user1", "user1",
            Date.valueOf(LocalDate.of(2024, 1, 1)), Gender.MAN, "서울시", 36.5, 127.5);
        Long userId1 = userService.makeUser(user1);
        User newUser1 = userService.findByUserId(userId1);
        Zipsa newZipsa1 = Zipsa.builder().zipsaId(newUser1).account("111").description("Asd")
            .gradeId(grade).isWorked(true).kindnessAverage(1.0).replyAverage(1.0).rewindAverage(1.0)
            .skillAverage(1.0).serviceCount(0).preferTag("abc").replyCount(0).build();
        em.persist(newZipsa1);
        Long zipsaId1 = newZipsa1.getZipsaId().getUserId();
        Zipsa zipsa1 = zipsaService.findByZipsaId(zipsaId1);
        ZipsaCategory zipsaCategory1 = ZipsaCategory.builder().zipsaCategoryId(
            ZipsaCategoryId.builder().majorCategory(majorCategory).zipsa(zipsa1).build()).build();
        em.persist(zipsaCategory1);

        // 방 만들기
        Long roomId = roomService.makePublicRoom(
            new MakePublicRoomRequest(subCategory.getSubCategoryId(), "title",
                "content", "place", 2, Timestamp.valueOf("2024-01-01 01:01:01"),
                Timestamp.valueOf("2024-01-01 01:01:01"), Timestamp.valueOf("2024-01-01 01:01:01"),
                15000), user.getUserId());
        roomService.changeRoomZipsa(zipsa1, roomId);
        em.flush();
        em.clear();
        matchService.changeMatchEndedAt(roomId);
        em.flush();
        em.clear();
        Room room = roomService.findByRoomId(roomId);
        assertNotNull(room.getEndedAt());
    }
}