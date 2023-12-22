package com.github.vssavin.usman_webstatic.spring5;

import com.github.vssavin.usman_webstatic.spring5.config.ApplicationConfig;
import com.github.vssavin.usman_webstatic.spring5.config.UsmanTemplateResolverConfig;
import com.github.vssavin.usman_webstatic.spring5.user.UserDatabaseInitService;
import com.github.vssavin.usmancore.config.ArgumentsProcessedNotifier;
import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.config.UsmanSecureServiceArgumentsHandler;
import com.github.vssavin.usmancore.security.SecureService;
import com.github.vssavin.usmancore.spring5.user.User;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author vssavin on 22.12.2023.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("usman-test")
@SpringBootTest(args = { "usman.secureService=rsa" }, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = { ApplicationConfig.class, UsmanTemplateResolverConfig.class })
@WebAppConfiguration
public abstract class AbstractTest implements ArgumentsProcessedNotifier {

    private static final String DEFAULT_SECURE_ENDPOINT = "/usman/v1/security/key";

    protected MockMvc mockMvc;

    protected SecureService secureService;

    protected UserDatabaseInitService userDatabaseInitService;

    protected WebApplicationContext context;

    protected UsmanConfigurer usmanConfigurer;

    @Autowired
    public void setContext(WebApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setUsmanConfigurer(UsmanConfigurer usmanConfigurer) {
        this.usmanConfigurer = usmanConfigurer;
    }

    @Autowired
    public void setUserDatabaseInitService(UserDatabaseInitService dataBaseInitServiceUser) {
        this.userDatabaseInitService = dataBaseInitServiceUser;
    }

    @Override
    public void notifyArgumentsProcessed(Class<?> aClass) {
        if (aClass != null && UsmanSecureServiceArgumentsHandler.class.isAssignableFrom(aClass)) {
            this.secureService = usmanConfigurer.getSecureService();
        }
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .addFilter(((request, response, chain) -> {
                response.setCharacterEncoding("UTF-8");
                chain.doFilter(request, response);
            }))
            .build();
    }

    protected RequestPostProcessor getRequestPostProcessorForUser(User user) {
        return user(user.getLogin()).password(user.getPassword()).roles(user.getAuthority());
    }

    protected String encrypt(String secureEndpoint, String data) throws Exception {
        String urlTemplate = secureEndpoint;
        if (urlTemplate == null || urlTemplate.isEmpty()) {
            urlTemplate = DEFAULT_SECURE_ENDPOINT;
        }
        ResultActions secureAction = mockMvc.perform(get(urlTemplate));
        String secureKey = secureAction.andReturn().getResponse().getContentAsString();
        return secureService.encrypt(data, secureKey);
    }

    protected String decrypt(String secureEndpoint, String data) throws Exception {
        String urlTemplate = secureEndpoint;
        if (urlTemplate == null || urlTemplate.isEmpty()) {
            urlTemplate = DEFAULT_SECURE_ENDPOINT;
        }
        ResultActions secureAction = mockMvc.perform(get(urlTemplate));
        String secureKey = secureAction.andReturn().getResponse().getContentAsString();
        return secureService.decrypt(data, secureKey);
    }

    protected static User testAdminUser() {
        // StringBuilder used to create a new String (not from the string pool),
        // because if we use DefaultStringSafety - it changes the value of the object in
        // the string pool!
        User.UserBuilder builder = User.builder();
        return builder.login(new StringBuilder().append("admin").toString())
            .name(new StringBuilder().append("admin").toString())
            .password(new StringBuilder().append("admin").toString())
            .email("admin@example.com")
            .authority("ADMIN")
            .build();
    }

    protected static User testUser() {
        // StringBuilder used to create a new String (not from the string pool),
        // because if we use DefaultStringSafety - it changes the value of the object in
        // the string pool!
        return User.builder()
            .login(new StringBuilder().append("user").toString())
            .name(new StringBuilder().append("user").toString())
            .password(new StringBuilder().append("user").toString())
            .email("user@examle.com")
            .authority("USER")
            .build();
    }

}
