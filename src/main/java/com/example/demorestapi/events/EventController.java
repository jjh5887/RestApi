package com.example.demorestapi.events;


import com.example.demorestapi.common.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {


    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){ //Errors 는 @Valid 바로 옆에 있어야
        // NotEmpty NotMull Min(0) 에러 검증
        if (errors.hasErrors()){
            return badRequest(errors);
        }

        // Custom 에러 검증  ex) 이벤트 종료시간이 접수 시간보다 빠른지
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = this.modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);
        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI uri = linkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events")); // EventResource 안에 넣어 주는게 좋음
        eventResource.add(linkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(uri).body(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }


    // EventDto를 이용한 입력값 제한하기 -> EventDto 클래스에 있는 데이터 외에는 다 무시
//    @PostMapping
//    public ResponseEntity createEvent(@RequestBody EventDto eventDto){
//
//        Event event = this.modelMapper.map(eventDto, Event.class);
//        Event newEvent = eventRepository.save(event);
//
//        URI uri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
//        return ResponseEntity.created(uri).body(event);
//    }



}

