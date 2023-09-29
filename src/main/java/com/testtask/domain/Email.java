package com.testtask.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "Emails")
public class Email {

    @Id
    @Column(name = "email_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @jakarta.validation.constraints.Email(message = "Wrong email format")
    private String email;
}
