package com.example.demorestapi.events;

import com.example.demorestapi.accounts.Account;
import com.example.demorestapi.accounts.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id") // id 만 쓰는걸 추천 overflow 위험
@Entity
public class Event {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;  //(optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean free;
    private boolean offline;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = AccountSerializer.class) // manager 를 가져올 때 해당 Serializer 사용
    private Account manager;

    public void update() {
        eventStatus = EventStatus.DRAFT;
        //Update free
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }

        // Update offline
        if (this.location == null || this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
