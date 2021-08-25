package com.topolski.accountingapp.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class VerificationToken {
    @Id
    private String token;
    @OneToOne
    private User user;
}
