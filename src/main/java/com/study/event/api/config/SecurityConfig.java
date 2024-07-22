package com.study.event.api.config;

import com.study.event.api.auth.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

// 스프링 시큐리티 설정 파일
// 인터셉터, 필터 처리
// 세션인증, 토큰인증
// 권한처리
// OAuth2 - SNS 로그인
@RequiredArgsConstructor
// 컨트롤러에서 사전, 사후에 권한정보를 캐치해서 막을건지
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // 비밀번호 암호화 객체 컨테이너에 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정 (스프링 부트 2.7버전 이전에는 인터페이스를 통해 오버라이딩)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf().disable() // 필터설정 off
                .httpBasic().disable() // 베이직 인증 off
                .formLogin().disable() // 자체 로그인창 off
                // 세션 인증을 더이상 사용하지 않음 요기부터
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and() // 요까지
                .authorizeRequests() // 요청 별로 인가 설정
                .antMatchers(HttpMethod.DELETE,"/events/*").hasAnyAuthority("ADMIN", "")
                // permitAll 보다 먼저 넣어야 예외허용가능
                .antMatchers(HttpMethod.PUT, "/auth/promote").hasAuthority("COMMON")
                // 아래 요청은 로그인 인증 이전 모두 허용
                .antMatchers("/", "/auth/**", "/file/**").permitAll()
                // 나머지 요청은 인증이후 요청가능
                .anyRequest().authenticated() // 인가 설정 on (off = permitALl())
        ;

        // 토큰 위조검사 커스텀 필터 필터체인에 등록하기
        // CorsFilter(org.spring) 뒤에 커스텀 필터를 연결
        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);
        return http.build();
    }

}
