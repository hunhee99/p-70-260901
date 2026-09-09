package com.back.p67260811.domain.member.service;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.repository.MemberRepository;
import com.back.p67260811.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return memberRepository.count();
    }

    public Member join(String username, String password, String nickname) {
        return join(username, password, nickname, null);
    }

    public Member join(String username, String password, String nickname, String apiKey) {

        if (memberRepository.findByUsername(username).isPresent()) {
            throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
        }

        if(apiKey == null) {
            apiKey = UUID.randomUUID().toString();
        }

        Member member = new Member(username, passwordEncoder.encode(password), nickname, apiKey);
        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
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

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void checkPassword(String inputPassword, String rawPassword) {
        if(!passwordEncoder.matches(inputPassword, rawPassword)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }
    }
}