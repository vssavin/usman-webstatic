package com.github.vssavin.usman_webstatic.spring5.user;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.security.auth.UsmanUsernamePasswordAuthenticationToken;
import com.github.vssavin.usmancore.spring5.auth.AuthService;
import com.github.vssavin.usmancore.spring5.user.User;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.github.vssavin.usmancore.user.UsmanUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * @author vssavin on 22.12.2023.
 */
@Service
public class UserDatabaseInitService {

    private static final int DEFAULT_USERS_COUNT = 10;

    private final Logger log = LoggerFactory.getLogger(UserDatabaseInitService.class);

    private final UsmanUserService userService;

    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    private final int countUsers;

    private final Map<String, String> passwordHashes = new HashMap<>();

    public UserDatabaseInitService(UsmanUserService userService, AuthService authService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        String countUsersString = System.getProperty("userGenerator.count");
        int tmpCountUsers = DEFAULT_USERS_COUNT;
        if (countUsersString != null) {
            try {
                tmpCountUsers = Integer.parseInt(countUsersString);
            }
            catch (NumberFormatException e) {
                log.error("'userGenerator.count' property should be integer number");
            }
        }

        countUsers = tmpCountUsers;
    }

    @PostConstruct
    public void initUserDatabase() {

        try {
            userService.getUserByLogin("admin");
        }
        catch (UsernameNotFoundException e) {
            initUsers();
            UsmanUser adminUser = userService.getUserByLogin("admin");
            HttpServletRequest request = new MockHttpServletRequest();

            authService.processSuccessAuthentication(
                    new UsmanUsernamePasswordAuthenticationToken(adminUser.getLogin(), adminUser.getPassword()),
                    request, EventType.LOGGED_IN);
        }
    }

    private void initUsers() {
        Map<User, Role> userRoleMap = new HashMap<>();
        userRoleMap.put(User.builder().login("admin").email("admin@example.com").password("admin").build(),
                Role.ROLE_ADMIN);
        userRoleMap.put(User.builder().login("user").email("user@example.com").password("user").build(),
                Role.ROLE_USER);
        userRoleMap.put(User.builder().login("user_new").email("user_new@example.com").password("user_new").build(),
                Role.ROLE_USER);

        for (int i = 0; i < countUsers; i++) {
            String login = String.valueOf(i);
            String name = String.valueOf(i);
            String password = passwordHashes.get(login);
            if (password == null) {
                password = passwordEncoder.encode(login);
                passwordHashes.put(login, password);
            }
            String email = login + "@" + login + ".com";
            Role role = Role.ROLE_USER;
            userRoleMap.put(User.builder().login(login).name(name).email(email).password(password).build(), role);
        }

        userRoleMap.forEach((user, role) -> {
            try {
                userService.registerUser(user.getLogin(), user.getUsername(),
                        passwordEncoder.encode(user.getPassword()), user.getEmail(), role);
            }
            catch (UsernameNotFoundException e) {
                // ignore
            }
        });
    }

    public UsmanUserService getUserService() {
        return userService;
    }

}
