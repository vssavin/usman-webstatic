package com.github.vssavin.usman_webstatic.spring5.user;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.spring5.event.Event;
import com.github.vssavin.usmancore.spring5.event.EventRepository;
import com.github.vssavin.usmancore.spring5.user.User;
import com.github.vssavin.usmancore.spring5.user.UserRepository;
import com.github.vssavin.usmancore.spring5.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.*;

/**
 * @author vssavin on 22.12.2023.
 */
@Service
public class UserDatabaseInitService {

    private static final int DEFAULT_USERS_COUNT = 10;

    private final Logger log = LoggerFactory.getLogger(UserDatabaseInitService.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final int countUsers;

    private final Map<String, String> passwordHashes = new HashMap<>();

    private final Map<Long, User> initUsersMap = new HashMap<>();

    private final Map<Long, Event> initEventsMap = new HashMap<>();

    private Iterable<User> initUsers = new ArrayList<>();

    public UserDatabaseInitService(UserService userService, PasswordEncoder passwordEncoder,
            UserRepository userRepository, EventRepository eventRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
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

        restoreDatabase();

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

    private void restoreDatabase() {
        if (initEventsMap.isEmpty()) {
            eventRepository.findAll().forEach(event -> initEventsMap.put(event.getUserId(), event));
        }

        if (!initUsers.iterator().hasNext()) {
            initUsers = userRepository.findAll();
        }

        userRepository.deleteAll();

        if (initUsers.iterator().hasNext()) {
            Iterable<User> newUsers;

            newUsers = userRepository.saveAll(copyUsers(initUsers));
            for (User user : initUsers) {
                for (User newUser : newUsers) {
                    if (user.getLogin().equals(newUser.getLogin())) {
                        initUsersMap.put(user.getId(), newUser);
                    }
                }
            }
        }

        for (Map.Entry<Long, User> entry : initUsersMap.entrySet()) {
            Event event = initEventsMap.get(entry.getKey());
            if (event != null) {
                Event newEvent = new Event(entry.getValue().getId(), event.getEventType(), event.getEventTimestamp(),
                        event.getEventMessage(), entry.getValue());
                eventRepository.save(newEvent);
            }
        }
    }

    private List<User> copyUsers(Iterable<User> users) {
        List<User> newUsers = new ArrayList<>();
        for (User user : users) {
            User newUser = User.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .authority(user.getAuthority())
                .expirationDate(user.getExpirationDate())
                .verificationId(user.getVerificationId())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .build();
            newUsers.add(newUser);
        }

        return newUsers;
    }

}
