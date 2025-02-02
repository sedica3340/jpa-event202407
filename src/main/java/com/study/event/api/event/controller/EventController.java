package com.study.event.api.event.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.api.auth.TokenProvider;
import com.study.event.api.auth.TokenProvider.TokenUserInfo;
import com.study.event.api.event.dto.request.EventSaveDto;
import com.study.event.api.event.dto.response.EventOneDto;
import com.study.event.api.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;
    private final JPAQueryFactory factory;

    @GetMapping("/page/{pageNo}")
    public ResponseEntity<?> getList(
            @AuthenticationPrincipal TokenUserInfo tokenInfo,
            @RequestParam(required = false) String sort,
            @PathVariable int pageNo
    ) throws InterruptedException {


        if (sort == null) {
            return ResponseEntity.badRequest().body("sort 파라미터가 없더");
        }


        Map<String, Object> events = eventService.getEvents(sort, pageNo, tokenInfo);

//        Thread.sleep(2000);
        return ResponseEntity.ok().body(events);
    }

    @PostMapping
    public ResponseEntity<?> register(
            // JwtAuthFilter 에서 시큐리티에 등록한 데이터
            @AuthenticationPrincipal TokenUserInfo tokenInfo,
            @RequestBody EventSaveDto dto) {


        try {
            eventService.saveEvent(dto, tokenInfo);
            return ResponseEntity.ok().body("event saved!");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }


    }

    @PreAuthorize("hasAuthority('PREMIUM') or hasAuthority('ADMIN')")
    @GetMapping("/{eventId}")
    public ResponseEntity<?> detail(@PathVariable Long eventId) {

        if (eventId == null || eventId < 1) {
            String errorMessage = "eventId가 정확하지 않습니다";
            return ResponseEntity.badRequest().body(errorMessage);
        }

        EventOneDto eventDetail = eventService.getEventDetail(eventId);


        return ResponseEntity
                .ok()
                .body(eventDetail);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> delete(@PathVariable Long eventId) {

        eventService.deleteEvent(eventId);

        return ResponseEntity.ok().body("event deleted!");
    }

    @PatchMapping("/{eventId}")
    public  ResponseEntity<?> modify(@PathVariable Long eventId, @RequestBody EventSaveDto dto) {
        eventService.modifyEvent(dto, eventId);

        return ResponseEntity.ok().body("event modified!!");
    }


}
