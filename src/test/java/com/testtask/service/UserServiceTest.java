package com.testtask.service;

import com.testtask.dao.UserDAO;
import com.testtask.dao.impl.UserDAOImpl;
import com.testtask.domain.Email;
import com.testtask.domain.User;
import com.testtask.service.impl.UserServiceImpl;
import com.testtask.util.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SpringBootTest
public class UserServiceTest {
    @MockBean
    private UserDAO dao;
    @Autowired
    private UserService service;

    @Test
    public void shouldFindById() {

        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2000-12-30"))
                .build();

        Mockito.when(dao.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.ofNullable(user));

        User byId = service.findById(1);

        Assertions.assertNotNull(byId);
        Assertions.assertEquals("123@gmail.com", byId.getEmail().getEmail());
    }

    @Test
    public void tryFindNonExistentUser() {

        Mockito.when(dao.findById(ArgumentMatchers.any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(UserException.class, () -> service.findById(17), "User with this id wasn't found");
    }

    @Test
    public void shouldSaveUser() {

        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2000-12-30"))
                .build();

        Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
            User user1 = invocationOnMock.getArgument(0);
            user1.setId(12);
            return null;
        }).when(dao).save(ArgumentMatchers.any(User.class));

        service.save(user);

        Assertions.assertNotEquals(0, user.getId());
        Assertions.assertEquals("123@gmail.com", user.getEmail().getEmail());
        Assertions.assertEquals("Arturo", user.getFirstName());
    }

    @Test
    public void trySaveUserWithWrongAge() {

        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2010-12-30")) //Wrong age
                .build();

        Assertions.assertThrows(UserException.class, () -> service.save(user), "This user is under 18");
    }

    @Test
    public void testUpdateUser() {
        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User updatedUser = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2000-12-30"))
                .build();

        UserDAO userDao = Mockito.mock(UserDAOImpl.class);

        UserService userService = new UserServiceImpl(userDao);

        updatedUser.setId(1);
        userService.update(updatedUser);
        Mockito.verify(userDao).update(updatedUser);
    }

    @Test
    public void testUpdateUserWithWrongAge() {
        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User updatedUser = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2020-12-30")) //Wrong age
                .build();

        Assertions.assertThrows(UserException.class, () -> service.update(updatedUser), "This user is under 18");
    }

    @Test
    public void shouldFindUsersByBirthdateBetween2Dates(){
        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User
                .builder()
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2000-07-20"))
                .build();

        Email email2 = Email
                .builder()
                .email("456@gmail.com")
                .build();

        User user2 = User
                .builder()
                .email(email2)
                .firstName("Bill")
                .lastName("Gates")
                .birthdate(LocalDate.parse("1970-12-30"))
                .build();

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        Mockito.when(dao.findUsersByBirthdateBetween
                (ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(users);

        Assertions.assertEquals(2, service.findUsersByBirthdateBetween(LocalDate.MIN, LocalDate.MAX).size());


    }
}
