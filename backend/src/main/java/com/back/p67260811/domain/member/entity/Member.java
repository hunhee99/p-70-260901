package com.back.p67260811.domain.member.entity;

import com.back.p67260811.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    private String nickname;

    // 나중에 활용 -> 리프레시 토큰
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

    public Member(int id, String username, String nickname) {
        this.setId(id);
        this.username = username;
        this.nickname = nickname;
    }


    public boolean isAdmin() {
        return "admin".equals(this.username);
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }
}
