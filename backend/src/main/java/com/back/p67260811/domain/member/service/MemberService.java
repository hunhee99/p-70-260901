package com.back.p67260811.domain.member.service;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.repository.MemberRepository;
import com.back.p67260811.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;

    public long count() {
        return memberRepository.count();
    }

    public Member join(String username, String password, String nickname) {
        findByUsername(username).ifPresent((member) -> {
            throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
        });

        Member member = new Member(username, password, nickname);
        return memberRepository.save(member);
    }

    // 테스트용
    public Member join(String username, String password, String nickname, String apiKey) {
        if(apiKey == null) {
            apiKey = UUID.randomUUID().toString();
        }

        findByUsername(username).ifPresent((member) -> {
            throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
        });

        Member member = new Member(username, password, nickname, apiKey);
        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByApiKey(String apikey) {
        return memberRepository.findByApiKey(apikey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public Map<String, Object> payloadOrNull(String jwt) {
        return authTokenService.payloadOrNull(jwt);
    }

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }
}
