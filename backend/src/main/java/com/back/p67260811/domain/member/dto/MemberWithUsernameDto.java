package com.back.p67260811.domain.member.dto;

import com.back.p67260811.domain.member.entity.Member;

import java.time.LocalDateTime;

public record MemberWithUsernameDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String username,
        String nickname

) {
    public MemberWithUsernameDto(Member member) {
        this(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getUsername(),
                member.getNickname()
        );
    }
}
