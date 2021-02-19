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
public class RoomUserService {

    private final ConcurrentMap<Integer, User> users = new ConcurrentHashMap<>();

    public void add(final User user) {
        this.users.put(user.getId(), user);
    }

    public Optional<User> findById(final int id) {
        final User user = this.users.get(id);

        return Objects.isNull(user) ? Optional.empty() : Optional.of(user);
    }

    public void delete(final int id) {
        this.users.remove(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(this.users.values());
    }

}
