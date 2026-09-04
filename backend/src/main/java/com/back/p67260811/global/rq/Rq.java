package com.back.p67260811.global.rq;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.service.MemberService;
import com.back.p67260811.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;
import java.util.Optional;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {

    private final MemberService memberService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public Member getActor() {

        String headerAuthorization = getHeader("Authorization", "");

        String apiKey = "";
        String accessToken = "";

        if(!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "헤더의 인증 정보 형식이 올바르지 않습니다.");
            }

            String[] headerAuthorizationBits = headerAuthorization.split(" ", 3);

            if (headerAuthorizationBits.length < 2 || headerAuthorizationBits[1].isBlank()) {
                throw new ServiceException("401-2", "헤더의 인증 정보 형식이 올바르지 않습니다.");
            }


            apiKey = headerAuthorizationBits[1];
            accessToken = headerAuthorizationBits.length == 3 ? headerAuthorizationBits[2] : "";
        } else {
            Cookie[] cookies = request.getCookies();

            if(cookies == null) {
                throw new ServiceException("401-1", "인증 정보가 없습니다.");
            }

            for(Cookie cookie : cookies) {
                switch (cookie.getName()) {
                    case "apiKey" -> apiKey = cookie.getValue();
                    case "accessToken" -> accessToken = cookie.getValue();
                }
            }
        }



        Member member = null;

        if (!accessToken.isBlank()) {
            Map<String, Object> payload = memberService.payloadOrNull(accessToken);

            if (payload != null) {
                int id = (int) payload.get("id");
                member = memberService.findById(id)
                        .orElseThrow(() -> new ServiceException("401-4", "accessToken의 id에 해당하는 회원이 존재하지 않습니다."));
            }
        }

        if (member == null) {
            if (apiKey.isBlank()){
                throw new ServiceException("401-1", "로그인 후 이용해주세요.");
            }
            member = memberService
                    .findByApiKey(apiKey)
                    .orElseThrow(() -> new ServiceException("401-3", "API 키가 유효하지 않습니다."));
        }

        return member;

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

    private String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getHeader(name))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

}