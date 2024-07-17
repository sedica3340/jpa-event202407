package com.study.event.api.event.service;

import com.study.event.api.event.dto.request.EventSaveDto;
import com.study.event.api.event.dto.response.EventDetailDto;
import com.study.event.api.event.dto.response.EventOneDto;
import com.study.event.api.event.entity.Event;
import com.study.event.api.event.entity.EventUser;
import com.study.event.api.event.repository.EventRepository;
import com.study.event.api.event.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventUserRepository eventUserRepository;

    // 전체 조회 서비스
    public Map<String, Object> getEvents(String sort, int pageNo, String userId) {
        Pageable pageable = PageRequest.of(pageNo - 1, 4);

        Page<Event> eventsPage = eventRepository.findEvents(sort, pageable, userId);


//        List<Event> events = eventUser.getEventList();

        List<Event> events = eventsPage.getContent();

        long totalElements = eventsPage.getTotalElements();


        List<EventDetailDto> eventDtoList = events
                .stream().map(EventDetailDto::new)
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("events", eventDtoList);
        map.put("totalCount", totalElements);
        return map;
    }

    public void saveEvent(EventSaveDto dto, String userId) {

        EventUser eventUser = eventUserRepository.findById(userId).orElseThrow();
        Event newEvent = dto.toEntity();
        newEvent.setEventUser(eventUser);


        Event savedEvent = eventRepository.save(newEvent);
        log.info("saved event: {}", savedEvent);
    }

    public EventOneDto getEventDetail(Long id) {
        Event foundEvent = eventRepository.findById(id).orElseThrow();
        return new EventOneDto(foundEvent);

    }

    public void deleteEvent(Long id) {

        eventRepository.deleteById(id);
    }

    public void modifyEvent(EventSaveDto dto, Long id) {
        Event foundEvent = eventRepository.findById(id).orElseThrow();
        foundEvent.changeEvent(dto);

        eventRepository.save(foundEvent);

    }
}
