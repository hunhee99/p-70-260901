package com.back.p67260811.domain.member.controller;

import com.back.p67260811.domain.member.dto.MemberDto;
import com.back.p67260811.domain.member.entity.Member;
import com.back.p67260811.domain.member.service.MemberService;
import com.back.p67260811.global.dto.RsData;
import com.back.p67260811.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;

    record JoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,

            @NotBlank
            @Size(min = 2, max = 30)
            String password,

            @NotBlank
            @Size(min = 2, max = 30)
            String nickname
    ) {
    }

    record JoinResBody(
            MemberDto memberDto
    ) {
    }

    @PostMapping("/join")
    public RsData<MemberDto> join(
            @RequestBody @Valid JoinReqBody reqBody
    ) {

        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData(
                "201-1",
                "회원가입이 완료되었습니다. %s님 환영합니다.".formatted(reqBody.nickname),
                new JoinResBody(
                        new MemberDto(member)
                )
        );
    }


    record LoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,

            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    record LoginResBody(
            MemberDto memberDto,
            String apiKey
    ){}

    @PostMapping("/login")
    public RsData<MemberDto> login(
            @RequestBody @Valid LoginReqBody reqBody
    ) {

        // 1. 회원 존재 여부
        Member actor = memberService.findByUsername(reqBody.username).orElseThrow(
                () -> new ServiceException("401-1", "존재하지 않는 회원입니다.")
        );

        // 2. 존재하면 비밀 번호 체크
        if(!actor.getPassword().equals(reqBody.password)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호가 일치하면 인증 데이터(apiKey) 제공
        return new RsData(
                "200-1",
                "%s님 반갑습니다.".formatted(actor.getNickname()),
                new LoginResBody(
                        new MemberDto(actor),
                        actor.getApiKey()
                )
        );
    }
}
