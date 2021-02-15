package com.cursor.library;

import com.cursor.library.daos.UserDao;
import com.cursor.library.models.User;
import com.cursor.library.models.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Set;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class LibraryApplication {
    private final UserDao userDao;
    private final BCryptPasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @PostConstruct
    public void addUsers() {
        User user1 = new User();
        user1.setUserName("admin");
        user1.setPassword(encoder.encode("admin"));
        user1.setPermissions(Set.of(UserPermission.ROLE_ADMIN));
        userDao.save(user1);
        User user2 = new User();
        user2.setUserName("Max");
        user2.setPassword(encoder.encode("123"));
        user2.setPermissions(Set.of(UserPermission.ROLE_READ, UserPermission.ROLE_WRITE));
        userDao.save(user2);
    }
}
