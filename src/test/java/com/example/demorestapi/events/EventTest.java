package com.example.demorestapi.events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class) // JUnitParamsRunner 사용하기 위한 어노테이션
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String name = "Event";
        event.setName(name);
        event.setDescription("Spring");

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo("Spring");
    }

    @Test
//  @Parameters(method = "parametersForTestFree") // 테스트 코드에 중복된 코드를 줄여줌
//  함수 이름을 parametersFor[사용하려는 Test 함수이름] 으로 해주면 @Parameters 써도 가능 단 Test 함수 이름의 첫글자는 대문자로 작성!
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    // @Parameters 에 쓸 parameter 함수
    private Object[] parametersForTestFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{2, 0, false},
                new Object[]{0, 1000, false},
                new Object[]{100, 200, false}
        };
    }

    @Test
    @Parameters
    public void testOffline(String location, boolean isOffline) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] parametersForTestOffline() {
        return new Object[]{
                new Object[]{"강남", true},
                new Object[]{null, false},
                new Object[]{"   ", false}
        };
    }

}