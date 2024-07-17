package com.study.event.api.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.api.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.event.api.event.entity.QEvent.event;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Event> findEvents(String sort, Pageable pageable, String userId) {
        List<Event> eventList = factory
                .selectFrom(event)
                .where(event.eventUser.id.eq(userId))
                .orderBy(specifier(sort))
                .offset((pageable.getOffset()))
                .limit(pageable.getPageSize())
                .fetch();
        Long count = factory
                .select(event.count())
                .from(event)
                .where(event.eventUser.id.eq(userId))
                .fetchOne();


        return new PageImpl<>(eventList, pageable, count);
    }

    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "date":
                return event.date.desc();
            case "title":
                return event.title.asc();
            default:
                return null;
        }
    }
}
