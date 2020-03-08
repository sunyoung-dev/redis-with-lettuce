package com.example.lettuce.controller;

import com.example.lettuce.model.Person;
import com.example.lettuce.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepository personRepository;

    @Autowired
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}")
    public Person get(@PathVariable long id) {
        return personRepository.findById(id);
    }

    @PostMapping
    public String add(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable long id) {
        return personRepository.deleteById(id);
    }
}
