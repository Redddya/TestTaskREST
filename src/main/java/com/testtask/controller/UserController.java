package com.testtask.controller;

import com.testtask.domain.User;
import com.testtask.service.UserService;
import com.testtask.util.UserException;
import com.testtask.util.error_response.UserErrorResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 2. It has the following functionality:
 * 2.1. Create user. It allows to register users who are more than [18] years old.
 * The value [18] should be taken from properties file.
 * 2.2. Update one/some user fields
 * 2.3. Update all user fields
 * 2.4. Delete user
 * 2.5. Search for users by birth date range. Add the validation which checks that “From” is less than “To”.  Should return a list of objects
 */

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/find-people-between-dates")
    public List<User> getUsersWithBirthdateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return userService.findUsersByBirthdateBetween(from, to);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.findById(id);
    }

    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(
            @RequestBody @Valid User user, BindingResult bindingResult) {
        hasErrors(bindingResult);
        userService.save(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable int id,
                                             @RequestBody @Valid User user, BindingResult bindingResult) {
        hasErrors(bindingResult);
        user.setId(id);
        userService.update(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> userExceptionHandler
            (UserException e) {
        UserErrorResponse response =
                new UserErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }

    private void hasErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage());
            }
            throw new UserException(errorMessage.toString());
        }
    }
}
