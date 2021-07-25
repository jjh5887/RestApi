package com.example.demorestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
//            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
//            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong"); //필드 에러
            errors.reject("wrongPrices", "Values fo prices are wrong"); // 글로벌 에러
        }
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EventTime is wrong");
        }

        //TODO beginEventDateTime ext...
    }
}
