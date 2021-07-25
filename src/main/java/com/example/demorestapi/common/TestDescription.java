package com.example.demorestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //target -> method
@Retention(RetentionPolicy.SOURCE) // 언제 타겟을 가져갈거
public @interface TestDescription {

    String value();

}
