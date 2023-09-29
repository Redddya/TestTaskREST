package com.testtask.repository;

import com.testtask.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findUsersByBirthdateBetween(LocalDate from, LocalDate to);
}
