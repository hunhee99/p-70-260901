package com.back.p67260811.domain.member.service;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.repository.MemberRepository;
import com.back.p67260811.standard.Ut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Value("${custom.jwt.secret-key}")
    private String secretPattern;

    @Value("${custom.jwt.expireMills}")
    private long expireMills;


    @Test
    @DisplayName("authTokenService 서비스가 존재한다.")
    void t1() {
        assertThat(authTokenService).isNotNull();
    }

    // 로직 자체가 올바른지 왕복 테스트
    @Test
    @DisplayName("jjwt 최신 방식으로 JWT 생성, {name=\"Paul\", age=23}")
    void t2() {

        byte[] keyBytes = secretPattern.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        // 발행 시간과 만료 시간 설정
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expireMills);

        Map<String, Object> payload = Map.of("name", "Paul", "age", 23);

        // JSON ==> Map
        String jwt = Jwts.builder()
                .claims(payload) // 내용
                .issuedAt(issuedAt) // 생성날짜
                .expiration(expiration) // 만료날짜
                .signWith(secretKey) // 키 서명
                .compact();

        Claims parsedPayload = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        assertThat(parsedPayload)
                .containsAllEntriesOf(payload);


        assertThat(jwt).isNotBlank();

        System.out.println("jwt = " + jwt);
    }

    // Ut.jwt.toString이 올바른지 테스트
    @Test
    @DisplayName("Ut.jwt.toString 를 통해서 JWT 생성, {name=\"Paul\", age=23}")
    void t3() {
        Map<String, Object> payload =  Map.of("name", "Paul", "age", 23);

        // JWT 생성
        String jwt = Ut.jwt.toString(
                secretPattern,
                expireMills,
                payload
        );

        assertThat(jwt).isNotBlank();

        // 유효성 검사
        boolean validResult = Ut.jwt.isValid(jwt, secretPattern);
        assertThat(validResult).isTrue();

        // JWT로 내용 복원
        Map<String, Object> parsedPayload = Ut.jwt.payloadOrNull(jwt, secretPattern);
        assertThat(parsedPayload)
                .containsAllEntriesOf(payload);

        System.out.println("jwt = " + jwt);
    }


    @Test
    @DisplayName("AuthTokenService를 통해서 accessToken 생성")
    void t4() {

        Member member1 = memberRepository.findByUsername("user3").get();
        String accessToken = authTokenService.genAccessToken(member1);

        Map<String, Object> payload = authTokenService.payloadOrNull(accessToken);

        assertThat(accessToken).isNotBlank();

        assertThat(payload).containsAllEntriesOf(
                Map.of(
                        "id", member1.getId(),
                        "username", member1.getUsername()
                )
        );

        System.out.println("accessToken = " + accessToken);

    }
}