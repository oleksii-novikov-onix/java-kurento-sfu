package com.onix.kurento.service;

import com.onix.kurento.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserService {

    private final ConcurrentMap<Integer, User> users = new ConcurrentHashMap<>();

    public User add(final String name) {
        final int id = this.users.size() + 1;

        final User user = new User(id, name);

        this.users.put(id, user);

        return user;
    }

    public Optional<User> findById(final int id) {
        final User user = this.users.get(id);

        return Objects.isNull(user) ? Optional.empty() : Optional.of(user);
    }

    public List<User> findAll() {
        return new ArrayList<>(this.users.values());
    }

}
