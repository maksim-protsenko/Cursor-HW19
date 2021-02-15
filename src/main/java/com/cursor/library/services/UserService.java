package com.cursor.library.services;

import com.cursor.library.daos.UserDao;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserDao userDao;
    private final PasswordEncoder encoder;
    static AccessDeniedException ACCESS_DENIED = new AccessDeniedException("Access denied");

    public UserService(UserDao userDao, @Lazy PasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    public UserDetails login(String username, String password) {
        var user = userDao.findByUserName(username).orElseThrow(() -> ACCESS_DENIED);
        if (!encoder.matches(password, user.getPassword())) {
            throw ACCESS_DENIED;
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUserName(username).orElseThrow();
    }
}
