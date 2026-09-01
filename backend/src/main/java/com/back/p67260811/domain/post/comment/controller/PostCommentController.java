package com.back.p67260811.domain.post.comment.controller;

import com.back.p67260811.domain.post.comment.dto.PostCommentDto;
import com.back.p67260811.domain.post.comment.entity.PostComment;
import com.back.p67260811.domain.post.post.entity.Post;
import com.back.p67260811.domain.post.post.service.PostService;
import com.back.p67260811.global.dto.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class PostCommentController {

    private final PostService postService;

    @GetMapping
    public List<PostCommentDto> list(
            @PathVariable int postId
    ) {
        Post post = postService.findById(postId).get();

        return post.getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    @GetMapping("/{commentId}")
    public PostCommentDto item(
            @PathVariable int postId,
            @PathVariable int commentId
    ) {
        Post post = postService.findById(postId).get();
        PostComment postComment = postService.findCommentById(post, commentId);

        return new PostCommentDto(postComment);
    }


    record CommentWriteForm(
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "댓글 내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {
    }

    @PostMapping
    @Transactional
    public RsData<PostCommentDto> write(
            @PathVariable int postId,
            @Valid CommentWriteForm form
    ) {

        Post post = postService.findById(postId).get();
        PostComment postComment = postService.writeComment(post, form.content);
        // DB 저장
        postService.flush();
        return new RsData<>(
                "201-1",
                "%d번 댓글이 성공적으로 등록되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }

    @DeleteMapping("/{commentId}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable int postId,
            @PathVariable int commentId
    ) {

        Post post = postService.findById(postId).get();
        postService.deleteComment(post, commentId);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(commentId)
        );

    }

    record CommentModifyForm(
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "댓글 내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {
    }

    @PatchMapping("/{commentId}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable int postId,
            @PathVariable int commentId,
            @Valid CommentModifyForm form
    ) {

        Post post = postService.findById(postId).get();
        postService.modifyComment(post, commentId, form.content);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(commentId)
        );

    }
}