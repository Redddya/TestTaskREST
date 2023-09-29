package com.testtask.service.impl;

import com.testtask.dao.UserDAO;
import com.testtask.domain.User;
import com.testtask.service.UserService;
import com.testtask.util.UserException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserServiceImpl implements UserService {
    private final UserDAO dao;

    @Value("${min_age}")
    private int minAge;

    public UserServiceImpl(UserDAO dao) {
        this.dao = dao;
    }


    @Override
    public User findById(int id) {
        Optional<User> foundUser = dao.findById(id);
        if (foundUser.isEmpty())
            throw new UserException("User with this id wasn't found");
        return foundUser.get();
    }

    @Override
    public void save(User user) {
        if (!isAgeValid(user.getBirthdate())) {
            throw new UserException("This user is under 18");
        }
        dao.save(user);
    }

    @Override
    public void update(User user) {
        if (!isAgeValid(user.getBirthdate())) {
            throw new UserException("This user is under 18");
        }
        dao.update(user);
    }

    @Override
    public void deleteById(int id) {
        dao.deleteById(id);
    }


    @Override
    public List<User> findUsersByBirthdateBetween(LocalDate from, LocalDate to) {
        return dao.findUsersByBirthdateBetween(from, to);
    }

    private boolean isAgeValid(LocalDate birthdate) {
        LocalDate today = LocalDate.now();
        LocalDate ageLimit = today.minusYears(minAge);
        return !birthdate.isAfter(ageLimit);
    }
}