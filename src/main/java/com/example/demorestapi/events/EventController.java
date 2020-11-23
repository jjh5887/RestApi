package com.example.demorestapi.events;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class EventController {

    @PostMapping("/api/events")
    public ResponseEntity createEvent(){

        URI uri = linkTo(methodOn(EventController.class).createEvent()).slash("{id").toUri();z
        return ResponseEntity.created(uri).build();
    }

}

