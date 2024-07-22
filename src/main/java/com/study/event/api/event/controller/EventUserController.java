package com.study.event.api.event.controller;

import com.study.event.api.auth.TokenProvider;
import com.study.event.api.auth.TokenProvider.TokenUserInfo;
import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.dto.request.LoginRequestDto;
import com.study.event.api.event.dto.response.LoginResponseDto;
import com.study.event.api.event.service.EventUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")

public class EventUserController {

    private final EventUserService eventUserService;

    // 이메일 중복확인 API
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean isDuplicate = eventUserService.checkEmailDuplicate(email);

        return ResponseEntity.ok().body(isDuplicate);
    }

    // 인증 코드 검증 API
    @GetMapping("/code")
    public ResponseEntity<?> verifiCode(String email, String code) {
        log.info("{}'s verify code is [ {} ]", email, code);

        boolean isMatch = eventUserService.isMatchCode(email, code);
        return ResponseEntity.ok().body(isMatch);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody EventUserSaveDto dto) {
        log.info("save User Info - {}", dto);
        try {
            eventUserService.confirmSignUp(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("saved success");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDto dto) {

        try {
            LoginResponseDto responseDto = eventUserService.authenticate(dto);
            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            // 서비스에서 예외 발생
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }

    }

    // premium 회원으로 등급업하는 요청처리
    @PutMapping("/promote")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo tokenInfo
            ) {


        try {
            LoginResponseDto dto = eventUserService.promoteToPremium(tokenInfo.getUserId());
            return ResponseEntity.ok().body(dto);
        } catch (NoSuchElementException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//        catch (SQLException e) {
//            return ResponseEntity.internalServerError().body("ㅈㅅ;");
//        }
    }
}
