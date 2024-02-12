package com.a407.back.controller;

import com.a407.back.config.constants.SuccessCode;
import com.a407.back.dto.comment.CommentChangeRequest;
import com.a407.back.dto.comment.CommentCreateRequest;
import com.a407.back.dto.util.ApiResponse;
import com.a407.back.model.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> makeComment(
        @RequestBody CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<>(SuccessCode.INSERT_SUCCESS,
                commentService.makeComment(commentCreateRequest)));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> changeComment(@PathVariable("commentId") Long commentId, @RequestBody CommentChangeRequest commentChangeRequest) {
        commentService.changeComment(commentId, commentChangeRequest);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponse<>(SuccessCode.UPDATE_SUCCESS, "댓글 수정 완료되었습니다."));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
        @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponse<>(SuccessCode.DELETE_SUCCESS, "댓글 삭제가 완료되었습니다."));
    }

}