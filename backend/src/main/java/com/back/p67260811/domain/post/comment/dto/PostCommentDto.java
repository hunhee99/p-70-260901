package com.back.p67260811.domain.post.comment.dto;

import com.back.p67260811.domain.post.comment.entity.PostComment;

import java.time.LocalDateTime;

public record PostCommentDto(
        int id,
        String content,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public PostCommentDto(PostComment postComment) {
        this(
                postComment.getId(),
                postComment.getContent(),
                postComment.getCreateDate(),
                postComment.getModifyDate()
        );
    }
}
