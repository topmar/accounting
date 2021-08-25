package com.topolski.accountingapp.model.repository;

import com.topolski.accountingapp.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, String> {
    VerificationToken findByToken(String token);
}
