package com.example.lettuce.repository;

import com.example.lettuce.model.Person;

public interface PersonRepository {
    Person findById(long id);

    String save(Person person);

    Long deleteById(long id);
}
