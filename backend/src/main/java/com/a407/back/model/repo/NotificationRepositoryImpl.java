package com.a407.back.model.repo;

import com.a407.back.domain.Notification;
import com.a407.back.domain.Notification.Status;
import com.a407.back.domain.QNotification;
import com.a407.back.domain.Room;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JPAQueryFactory query;

    private final EntityManager em;

    @Override
    public Notification findByNotificationId(Long notificationId) {
        return em.find(Notification.class, notificationId);
    }

    @Override
    public void changeNotificationStatusAcceptOrReject(Long notificationId, String status) {
        QNotification qNotification = QNotification.notification;
        query.update(qNotification).set(qNotification.status, Status.valueOf(status))
            .where(qNotification.notificationId.eq(notificationId)).execute();
    }

    @Override
    public void changeNotificationStatusClose(Room room) {
        QNotification qNotification = QNotification.notification;
        query.update(qNotification).set(qNotification.status, Status.CLOSE)
            .where(qNotification.roomId.roomId.eq(room.getRoomId()).and(qNotification.status.eq(Status.STANDBY))).execute();
    }

    @Override
    public void makeNotification(Notification notification) {
        em.persist(notification);
    }

    @Override
    public List<Notification> findByRoomIdList(Room room) {
        QNotification qNotification = QNotification.notification;
        return query.selectFrom(qNotification).where(qNotification.roomId.eq(room)).fetch();
    }

    @Override
    public void deleteNotification(Notification notification) {
        em.remove(notification);
    }

}
