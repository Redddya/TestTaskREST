package com.testtask.service;

import com.testtask.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User findById(int id);

    void save(User user);

    void update(User user);

    void deleteById(int id);

    List<User> findUsersByBirthdateBetween(LocalDate from, LocalDate to);
}
