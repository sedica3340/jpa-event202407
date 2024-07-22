package com.study.event.api.event.repository;


import com.study.event.api.auth.TokenProvider;
import com.study.event.api.auth.TokenProvider.TokenUserInfo;
import com.study.event.api.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {

    Page<Event> findEvents(String sort, Pageable pageable, TokenUserInfo tokenInfo);
}
