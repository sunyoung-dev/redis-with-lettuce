package com.example.lettuce.repository;

import com.example.lettuce.model.Person;
import com.example.lettuce.redis.LettuceTemplate;
import com.example.lettuce.redis.RedisValueOperations;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RedisPersonRepository implements PersonRepository {
    private static final String KEY = "person";
    private static final String ID = "id";
    private static final String DELIMITER = ":";

    private final Gson gson = new Gson();
    private final RedisValueOperations<String, String> valueOperations;

    @Autowired
    public RedisPersonRepository(LettuceTemplate<String, String> lettuceTemplate) {
        this.valueOperations = lettuceTemplate.getValueOperations();
    }

    private String key(long id) {
        return KEY + DELIMITER + id;
    }

    private String idKey() {
        return KEY + DELIMITER + ID;
    }

    @Override
    public Person findById(long id) {
        String json = valueOperations.get(key(id));
        return gson.fromJson(json, Person.class);
    }

    @Override
    public String save(Person person) {
        long id = valueOperations.incr(idKey());
        person.setId(id);

        return valueOperations.set(key(id), gson.toJson(person));
    }

    @Override
    public Long deleteById(long id) {
        return valueOperations.del(key(id));
    }
}
