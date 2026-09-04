package com.back.p67260811.domain.member.entity;

import com.back.p67260811.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    private String nickname;

    // 나중에 활용
    @Column(unique = true)
    private String apiKey;

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = UUID.randomUUID().toString();
    }

    public Member(String username, String password, String nickname, String apiKey) {
        this(username, password, nickname);
        this.apiKey = apiKey;
    }
}
