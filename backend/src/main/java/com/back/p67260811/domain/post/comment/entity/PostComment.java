package com.back.p67260811.domain.post.comment.entity;

import com.back.p67260811.domain.post.post.entity.Post;
import com.back.p67260811.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostComment extends BaseEntity {
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public void modify(String content) {
        this.content = content;
    }
}
