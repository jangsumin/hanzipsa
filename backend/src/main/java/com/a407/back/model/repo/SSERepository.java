package com.a407.back.model.repo;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSERepository {

    SseEmitter get(Long userId);

    void save(Long userId, SseEmitter sseEmitter);

    void delete(Long userId);
}
