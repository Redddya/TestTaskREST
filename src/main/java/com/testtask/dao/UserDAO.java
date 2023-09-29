package com.testtask.dao;

import com.testtask.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(int id);

    void save(User user);

    void update(User user);

    void deleteById(int id);

    List<User> findUsersByBirthdateBetween(LocalDate from, LocalDate to);
}
