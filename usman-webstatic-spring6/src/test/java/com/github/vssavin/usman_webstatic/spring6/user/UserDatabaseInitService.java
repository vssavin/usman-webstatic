package com.github.vssavin.usman_webstatic.spring6.user;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.spring6.user.User;
import com.github.vssavin.usmancore.spring6.user.UserRepository;
import com.github.vssavin.usmancore.spring6.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vssavin on 23.12.2023.
 */
@Service
public class UserDatabaseInitService {

    private static final int DEFAULT_USERS_COUNT = 10;

    private final Logger log = LoggerFactory.getLogger(UserDatabaseInitService.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final int countUsers;

    private final Map<String, String> passwordHashes = new HashMap<>();

    private Iterable<User> initUsers = new ArrayList<>();

    public UserDatabaseInitService(UserService userService, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
        if (!initUsers.iterator().hasNext()) {
            initUsers = userRepository.findAll();
        }
        userRepository.deleteAll();
        if (initUsers.iterator().hasNext()) {
            userRepository.saveAll(initUsers);
        }

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
            try {
                userService.registerUser(login, name, password, email, role);
            }
            catch (Exception e) {
                log.error("Register user error: ", e);
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

}
