package com.testtask.dao;

import com.testtask.domain.Address;
import com.testtask.domain.Email;
import com.testtask.domain.Phone;
import com.testtask.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserDAOTest {

    @Autowired
    private UserDAO userDAO;

    @TestConfiguration
    @ComponentScan(basePackages = "com.testtask.*")
    public static class Config {
    }

    @BeforeEach
    public void saveUser() {
        Address address = Address
                .builder()
                .city("Kiev")
                .country("Ukraine")
                .street("Shevchenko")
                .house("15A")
                .build();

        Phone phone = Phone
                .builder()
                .phone("0675442155")
                .build();

        Email email = Email
                .builder()
                .email("miller@gmail.com")
                .build();

        User userToSave = User
                .builder()
                .birthdate(LocalDate.parse("2002-03-29"))
                .firstName("John")
                .lastName("Miller")
                .email(email)
                .phone(phone)
                .address(address)
                .build();

        userDAO.save(userToSave);
    }

    @Test
    @DirtiesContext
    void shouldUpdateUser() {
        String testEmail = "testemail@gmail.com";

        Address address = Address
                .builder()
                .city("Kiev")
                .country("Ukraine")
                .street("Shevchenko")
                .house("15A")
                .build();

        Phone phone = Phone
                .builder()
                .phone("0675442155")
                .build();

        Email email = Email
                .builder()
                .email("miller@gmail.com")
                .build();

        User userToSave = User
                .builder()
                .birthdate(LocalDate.parse("2002-03-29"))
                .firstName("John")
                .lastName("Miller")
                .email(email)
                .phone(phone)
                .address(address)
                .build();

        userDAO.save(userToSave);

        User user = userDAO.findById(1).orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setEmail(Email.builder().email(testEmail).build());
        userDAO.update(user);

        User updatedUser = userDAO.findById(1).orElseThrow(() -> new NoSuchElementException("User not found"));
        Assertions.assertEquals(testEmail, updatedUser.getEmail().getEmail());
    }


    @Test
    @DirtiesContext
    void shouldSaveUser() {

        User author = userDAO.findById(1).get();

        Assertions.assertEquals("miller@gmail.com", author.getEmail().getEmail());
    }

    @Test
    @DirtiesContext
    void shouldDeleteUser() {
        userDAO.deleteById(1);
        boolean isAuthorExists = userDAO.findById(1).isEmpty();

        Assertions.assertTrue(isAuthorExists);
    }

    @Test
    @DirtiesContext
    void shouldFindById() {
        String email = "miller@gmail.com";

        Optional<User> byId = userDAO.findById(1);
        User user = null;
        if (byId.isPresent()) {
            user = byId.get();
        }
        Assertions.assertEquals(email, user.getEmail().getEmail());
        Assertions.assertEquals("John", user.getFirstName());
    }

    @Test
    @DirtiesContext
    void shouldFindUsersByBirthdateBetweenDates() {

        Address address = Address
                .builder()
                .city("Kiev")
                .country("Ukraine")
                .street("Shevchenko")
                .house("15A")
                .build();

        Phone phone = Phone
                .builder()
                .phone("0675442155")
                .build();

        Email email = Email
                .builder()
                .email("miller@gmail.com")
                .build();

        User userToSave = User
                .builder()
                .firstName("Artem")
                .lastName("Urbanenko")
                .birthdate(LocalDate.parse("1982-12-30"))
                .email(email)
                .phone(phone)
                .address(address)
                .build();

        userDAO.save(userToSave);

        List<User> usersByBirthdateBetween1 = userDAO.
                findUsersByBirthdateBetween(LocalDate.parse("1971-03-04"), LocalDate.parse("2003-03-04"));

        List<User> usersByBirthdateBetween2 = userDAO.
                findUsersByBirthdateBetween(LocalDate.parse("2000-03-04"), LocalDate.parse("2003-03-04"));

        Assertions.assertEquals(2, usersByBirthdateBetween1.size());
        Assertions.assertEquals(1, usersByBirthdateBetween2.size());
    }

}
