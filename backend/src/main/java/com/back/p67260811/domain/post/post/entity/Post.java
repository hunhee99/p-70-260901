package com.back.p67260811.domain.post.post.entity;

import com.back.p67260811.domain.post.comment.entity.PostComment;
import com.back.p67260811.global.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Post extends BaseEntity {
    private String title;
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }


    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    List<PostComment> comments = new ArrayList<>();

    public PostComment findCommentById(int id) {
        return comments
                .stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));
    }

    public PostComment addComment(String content) {
        PostComment postComment = new PostComment(content, this);
        this.comments.add(postComment);

        return postComment;
    }

    public void removeComment(int id) {
        comments.removeIf(comment -> comment.getId() == id);
    }

    public PostComment modifyComment(int id, String content) {
        PostComment postComment = comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));

        postComment.modify(content);

        return postComment;
    }

    public void update(String title, String content) {

        // 비즈니스 규칙
        this.title = title;
        this.content = content;
    }
}