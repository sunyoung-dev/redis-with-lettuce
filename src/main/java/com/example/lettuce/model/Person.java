package com.example.lettuce.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Person {
    private long id;
    private String name;
    private int age;
    private LocalDate birthDate;
    private String hobby;
}
