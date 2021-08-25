package com.topolski.accountingapp.services;

import com.topolski.accountingapp.model.User;
import com.topolski.accountingapp.model.UserRole;
import com.topolski.accountingapp.model.VerificationToken;
import com.topolski.accountingapp.model.repository.UserRepo;
import com.topolski.accountingapp.model.repository.VerificationTokenRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepo userRepo;
    private final VerificationTokenRepo verificationTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepo userRepo,
                       VerificationTokenRepo verificationTokenRepo,
                       PasswordEncoder passwordEncoder,
                       MailSenderService mailSenderService) {
        this.userRepo = userRepo;
        this.verificationTokenRepo = verificationTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    public void addNewUser(String username,
                           String password,
                           String firstName,
                           String lastName,
                           String tel,
                           String email,
                           boolean admin,
                           HttpServletRequest request) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .tel(tel)
                .email(email)
                .creationDate(
                        LocalDateTime.now().format(
                                DateTimeFormatter
                                        .ofPattern("yyyy-MM-dd HH:mm:ss")))
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .role(UserRole.USER.getRole())
                .build();
        userRepo.save(user);
        String token = UUID.randomUUID().toString();
        verificationTokenRepo.save(
                VerificationToken.builder()
                        .user(user)
                        .token(token)
                        .build());
        String url = "http://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath()
                + "/api/verify-token?token="
                + token;
        try {
            mailSenderService.sendMail(user.getEmail(), "verification token", url);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        if (admin) {
            String masterEmail = "";
            if (userRepo.existsByRole(UserRole.MASTER.getRole())) {
                masterEmail = userRepo.findByRole(UserRole.MASTER.getRole()).getEmail();
            } else {
                user.setRole(UserRole.MASTER.getRole());
                userRepo.save(user);
            }
            if (!masterEmail.isEmpty()) {
            token = UUID.randomUUID().toString().concat(UUID.randomUUID().toString());
            verificationTokenRepo.save(
                    VerificationToken.builder()
                            .user(user)
                            .token(token)
                            .build());
            url = "http://"
                    + request.getServerName()
                    + ":"
                    + request.getServerPort()
                    + request.getContextPath()
                    + "/api/verify-token?token="
                    + token;
                try {
                    mailSenderService.sendMail(masterEmail, "verification token", url);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkUserIfUsernameExists(String username) {
        return userRepo.existsByUsername(username);
    }
    public boolean checkUserIfEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    public void verifyToken(String token) {
        if (token.length() > 60) {
            setUserToAdmin(token);
        } else {
            setUserEnabled(token);
        }
    }

    public void setUserEnabled(String token) {
        User user = verificationTokenRepo.findByToken(token).getUser();
        user.setEnabled(true);
        userRepo.save(user);
        verificationTokenRepo.deleteById(token);
    }

    public void setUserToAdmin(String token) {
        User user = verificationTokenRepo.findByToken(token).getUser();
        user.setRole(UserRole.ADMIN.getRole());
        userRepo.save(user);
        verificationTokenRepo.deleteById(token);
    }
}
