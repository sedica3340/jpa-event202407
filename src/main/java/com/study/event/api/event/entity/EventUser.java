package com.study.event.api.event.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_event_user")
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUser {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "ev_user_id")
    private String id; // 랜덤 문자 pk

    @Column(name = "ev_user_email", nullable = false, unique = true)
    private String email;

    // NOTNULL을 하지않는 이유: SNS로그인한 회원, 인증번호만 받고 회원가입을 완료하지 않은 회원 처리
    @Column(length = 500)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.COMMON;

    private LocalDateTime createAt;

    // 엔터티에 boolean타입을 사용하면 실제 db에는 0, 1로 저장됨에 주의
    @Setter
    @Column(nullable = false)
    private boolean emailVerified;
}
