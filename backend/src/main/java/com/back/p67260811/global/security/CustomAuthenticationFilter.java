package com.back.p67260811.global.security;

import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.service.MemberService;
import com.back.p67260811.global.exception.ServiceException;
import com.back.p67260811.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final Rq rq;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("CustomAuthenticationFilter called");  // debug 모드일 때 출력

        // 인증 로직

        // api로 시작하는 URL이 아니라면 통과
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 회원가입, 로그인처럼 인증이 없는 사용자를 위한 api는 통과
        if (List.of("/api/v1/members/join", "/api/v1/members/login").contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey;
        String accessToken;

        String headerAuthorization = rq.getHeader("Authorization", "");

        if (!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer "))
                throw new ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.");

            String[] headerAuthorizationBits = headerAuthorization.split(" ", 3);

            apiKey = headerAuthorizationBits[1];
            accessToken = headerAuthorizationBits.length == 3 ? headerAuthorizationBits[2] : "";
        } else {
            apiKey = rq.getCookieValue("apiKey", "");
            accessToken = rq.getCookieValue("accessToken", "");
        }

        if (apiKey.isBlank())
            throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        Member member = null;

        boolean isAccessTokenExists = !accessToken.isBlank();
        boolean isAccessTokenValid = false;

        if (isAccessTokenExists) {
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
            member = memberService
                    .findByApiKey(apiKey)
                    .orElseThrow(() -> new ServiceException("401-3", "API 키가 유효하지 않습니다."));
        }

        if (isAccessTokenExists && !isAccessTokenValid) {
            String newAccessToken = memberService.genAccessToken(member);
            rq.addCookie("accessToken", newAccessToken);
            rq.setHeader("accessToken", newAccessToken);
        }




        filterChain.doFilter(request, response);
    }
}
