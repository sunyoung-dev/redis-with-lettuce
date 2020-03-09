package com.example.lettuce.repository;

import com.example.lettuce.model.Person;
import com.google.gson.Gson;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RedisPersonRepository implements PersonRepository {
    private static final String KEY = "person";
    private static final String ID = "id";
    private static final String DELIMITER = ":";

    private final Gson gson = new Gson();
    private final RedisCommands<String, String> redisSyncCommands;

    @Autowired
    public RedisPersonRepository(StatefulRedisConnection<String, String> connection) {
        this.redisSyncCommands = connection.sync();
    }

    private String key(long id) {
        return KEY + DELIMITER + id;
    }

    private String idKey() {
        return KEY + DELIMITER + ID;
    }

    @Override
    public Person findById(long id) {
        String json = redisSyncCommands.get(key(id));
        return gson.fromJson(json, Person.class);
    }

    @Override
    public String save(Person person) {
        long id = redisSyncCommands.incr(idKey());
        person.setId(id);

        return redisSyncCommands.set(key(id), gson.toJson(person));
    }

    @Override
    public Long deleteById(long id) {
        return redisSyncCommands.del(key(id));
    }
}
