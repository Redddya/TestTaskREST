package com.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testtask.domain.Address;
import com.testtask.domain.Email;
import com.testtask.domain.Phone;
import com.testtask.domain.User;
import com.testtask.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @TestConfiguration
    @ComponentScan(basePackages = "com.testtask.*")
    public static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }
    }

    @Test
    public void shouldCreateUser() throws Exception {
        Mockito.doNothing().when(service).save(ArgumentMatchers.any(User.class));
        Address address = Address
                .builder()
                .city("Kiev")
                .country("Ukraine")
                .street("Shevchenko")
                .house("15A")
                .build();

        Phone phone = Phone
                .builder()
                .phone("0672443456")
                .build();

        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User
                .builder()
                .address(address)
                .phone(phone)
                .email(email)
                .firstName("Arturo")
                .lastName("Roman")
                .birthdate(LocalDate.parse("2000-12-30"))
                .build();

        OBJECT_MAPPER.findAndRegisterModules();
        String json = OBJECT_MAPPER.writeValueAsString(user);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/users/new")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void tryCreateUserWithoutName() throws Exception {
        Mockito.doNothing().when(service).save(ArgumentMatchers.any(User.class));

        Email email = Email
                .builder()
                .email("123@gmail.com")
                .build();

        User user = User //No last and first names
                .builder()
                .email(email)
                .birthdate(LocalDate.parse("2000-12-30"))
                .build();

        OBJECT_MAPPER.findAndRegisterModules();
        String json = OBJECT_MAPPER.writeValueAsString(user);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/users/new")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        Mockito.doNothing().when(service).deleteById(ArgumentMatchers.any(Integer.class));

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/users/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        Mockito.doNothing().when(service).update(ArgumentMatchers.any(User.class));

        User user = User
                .builder()
                .firstName("Bob")
                .lastName("Winston")
                .birthdate(LocalDate.parse("2002-12-31"))
                .email(Email.builder().email("bobWinston@gmail.com").build())
                .build();

        OBJECT_MAPPER.findAndRegisterModules();
        String json = OBJECT_MAPPER.writeValueAsString(user);

        mockMvc
                .perform(MockMvcRequestBuilders.patch("/users/" + user.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void shouldGetUsersWithAgeBetween() throws Exception {

        User user = User
                .builder()
                .id(1)
                .firstName("Bob")
                .lastName("Winston")
                .birthdate(LocalDate.parse("2002-12-31"))
                .email(Email.builder().email("bobWinston@gmail.com").build())
                .build();

        User user1 = User
                .builder()
                .id(2)
                .firstName("Bill")
                .lastName("Fire")
                .birthdate(LocalDate.parse("2001-11-06"))
                .email(Email.builder().email("billfire@gmail.com").build())
                .build();

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);

        Mockito.when(service.findUsersByBirthdateBetween(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(users);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/users/find-people-between-dates?from=2000-11-12&to=2003-07-02", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Bob"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Bill"));
        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    public void shouldGetRubric() throws Exception {

        User user = User
                .builder()
                .id(1)
                .firstName("Bob")
                .lastName("Winston")
                .birthdate(LocalDate.parse("2002-12-31"))
                .email(Email.builder().email("bobWinston@gmail.com").build())
                .build();

        Mockito.when(service.findById(ArgumentMatchers.anyInt())).thenReturn(user);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/users/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Bob"));

    }
}