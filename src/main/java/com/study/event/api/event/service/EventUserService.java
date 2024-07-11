package com.study.event.api.event.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.entity.EmailVerification;
import com.study.event.api.event.entity.EventUser;
import com.study.event.api.event.repository.EmailVerificationRepository;
import com.study.event.api.event.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventUserService {

    private final EventUserRepository eventUserRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;
    private final JPAQueryFactory factory;
    @Value("${study.mail.host}")
    private String mailHost;

    public boolean checkEmailDuplicate(String email) {
        boolean exists = eventUserRepository.existsByEmail(email);
        log.info("checking email {} is duplicate : {}", email, exists);
        if (!exists) {
            processSignUp(email);
        }
        return exists;
    }

    public void processSignUp(String email) {
        EventUser newEventUser = EventUser.builder()
                .email(email)
                .build();
        EventUser savedUser = eventUserRepository.save(newEventUser);

        generateAndSendCode(email, savedUser);

    }

    private void generateAndSendCode(String email, EventUser savedUser) {
        String code = sendVerificationEmail(email);

        EmailVerification verification = EmailVerification.builder()
                .verificationCode(code)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .eventUser(savedUser)
                .build();

        emailVerificationRepository.save(verification);
    }

    public String sendVerificationEmail(String email) {

        // 검증 코드 생성하기
        String code = generateVerificationCode();

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            messageHelper.setTo(email);

            messageHelper.setSubject("[인증메일] 중앙정보스터디 가입 인증 메일입니다.");

            messageHelper.setText("인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>", true);

            messageHelper.setFrom(mailHost);

            mailSender.send(mimeMessage);

            log.info("{} 님에게 이메일 전송!", email);

            return code;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 이메일을 전송할 객체 생성

    }

    // 검증코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {


        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }

    public boolean isMatchCode(String email, String code) {
        // 이메일을 통해 회원정보를 탐색
        EventUser eventUser = eventUserRepository.findByEmail(email).orElse(null);

        if (eventUser != null) {
            EmailVerification ev = emailVerificationRepository.findByEventUser(eventUser).orElse(null);

            // 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && code.equals(ev.getVerificationCode())) {

                // 이메일 이증여부 true 로 수정
                eventUser.setEmailVerified(true);
                eventUserRepository.save(eventUser);

                // 인증코드 데이터베이스에서 삭제
                emailVerificationRepository.delete(ev);

                return true;
            } else { // 인증코드가 틀렸거나 만료된 경우
                //인증코드 재발송
                emailVerificationRepository.delete(ev);

                generateAndSendCode(email, eventUser);
                // 기존 인증 코드 삭제
                // 새 인증코드 발급 이메일 재전송
                // 데이터베이스에 새 인증코드 저장

            }
        }
        return false;
    }

    // 회원가입 마무리
    public void confirmSignUp(EventUserSaveDto dto) {
        EventUser foundUser = eventUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("회원 정보가 존재하지 않습니다.")
                );

//        데이터 반영
        foundUser.confirm(dto.getPassword());

        eventUserRepository.save(foundUser);
    }
}
