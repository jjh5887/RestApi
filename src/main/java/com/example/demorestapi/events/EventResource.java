package com.example.demorestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// BeanSerializer -> 사용해서 자동으로 멤버 객체 json 변환
public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link...links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
//        add(new Link("http://localhost:8080/api/events/" + event.getId()));  // 위와 같으나 타입세이프 하지 않음
    }
}
