package com.example.demorestapi.events;


import com.example.demorestapi.accounts.Account;
import com.example.demorestapi.accounts.CurrentUser;
import com.example.demorestapi.common.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {


    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account user) { //Errors 는 @Valid 바로 옆에 있어야
        // NotEmpty NotMull Min(0) 에러 검증
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // Custom 에러 검증  ex) 이벤트 종료시간이 접수 시간보다 빠른지
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = this.modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(user);
        Event newEvent = eventRepository.save(event);
        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI uri = linkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events")); // EventResource 안에 넣어 주는게 좋음
        eventResource.add(linkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(uri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                      // @AuthenticationPrincipal User user
                                      // @AuthenticationPrincipal AccountAdapter user
                                      // @AuthenticationPrincipal(expression = "account") Account user
                                      // SpEL 이용
                                      @CurrentUser Account user) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (User) authentication.getPrincipal();

        Page<Event> all = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = assembler.toModel(all, e -> new EventResource(e));
        if (user != null) {
            entityModels.add(linkTo(EventController.class).withRel("create-event"));
        }
        entityModels.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id, @CurrentUser Account user) {
        Optional<Event> byId = this.eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = byId.get();
        EventResource body = new EventResource(event);
        body.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManager().equals(user)) {
            body.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(body);

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account user) {
        Optional<Event> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event existingEvent = byId.get();
        if (!existingEvent.getManager().equals(user)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
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

