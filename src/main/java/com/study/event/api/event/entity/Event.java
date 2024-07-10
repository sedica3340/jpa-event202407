package com.study.event.api.event.entity;

import com.study.event.api.event.dto.request.EventSaveDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ev_id")
    private Long id;

    @Column(name = "ev_title", nullable = false, length = 50)
    private String title;

    @Column(name = "ev_desc")
    private String description;

    @Column(name = "ev_image_path")
    private String image;

    @Column(name = "ev_start_date")
    private LocalDate date;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void changeEvent(EventSaveDto dto) {

        this.title = dto.getTitle();
        this.date = dto.getBeginDate();
        this.image = dto.getImageUrl();
        this.description = dto.getDesc();
    }

}
