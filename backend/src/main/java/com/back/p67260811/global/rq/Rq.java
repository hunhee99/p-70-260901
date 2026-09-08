package com.back.p67260811.global.rq;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.service.MemberService;
import com.back.p67260811.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {

    private final MemberService memberService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public Member getActor() {

        String headerAuthorization = getHeader("Authorization", "");

        String apiKey = null;
        String accessToken = null;

        if(!headerAuthorization.isBlank()) {

            if (!headerAuthorization.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "헤더의 인증 정보 형식이 올바르지 않습니다.");
            }

            String[] headerAuthorizationBits = headerAuthorization.split(" ", 3);

            apiKey = headerAuthorizationBits[1];
            accessToken = headerAuthorizationBits.length == 3 ? headerAuthorizationBits[2] : "";
        } else {
            apiKey = getCookieValue("apiKey", "");
            accessToken = getCookieValue("accessToken", "");
        }

        log.info("header={}, apiKey={}, accessTokenLen={}",
                headerAuthorization, apiKey, accessToken.length());

        Member member = null;

        boolean isAccessTokenExists = !accessToken.isBlank();
        boolean isAccessTokenValid = false;

        if (!accessToken.isBlank()) {
            Map<String, Object> payload = memberService.payloadOrNull(accessToken);

            if (payload != null) {
                int id = (int) payload.get("id");
                String username = (String) payload.get("username");
                String nickname = (String) payload.get("nickname");

                member = new Member(id, username, nickname);
                isAccessTokenValid = true;
            }
        }

        if (member == null) {

            if (apiKey.isBlank())
                throw new ServiceException("401-1", "API 키 정보가 존재하지 않습니다.");

            member = memberService
                    .findByApiKey(apiKey)
                    .orElseThrow(() -> new ServiceException("401-3", "API 키가 유효하지 않습니다."));
        }

        if (isAccessTokenExists && !isAccessTokenValid) {
            String newAccessToken = memberService.genAccessToken(member);
            addCookie("accessToken", newAccessToken);
            setHeader("accessToken", newAccessToken);
        }

        return member;

    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public String getCookieValue(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getCookies())
                .flatMap(
                        cookies ->
                                Arrays.stream(cookies)
                                        .filter(cookie -> cookie.getName().equals(name))
                                        .map(Cookie::getValue)
                                        .filter(value -> !value.isBlank())
                                        .findFirst()
                )
                .orElse(defaultValue);
    }

    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }

    public void deleteCookie(String name) {

        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

    }

    public String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getHeader(name))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

}