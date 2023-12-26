package com.github.vssavin.usman_webstatic.spring6.api.v1.event;

import com.github.vssavin.usman_webstatic.spring6.AbstractTest;
import com.github.vssavin.usmancore.event.EventType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.github.vssavin.usman_webstatic.spring6.api.v1.event.EventController.EVENT_CONTROLLER_PATH;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author vssavin on 25.12.2023.
 */
public class EventControllerTest extends AbstractTest {

    @Before
    public void initDatabase() {
        userDatabaseInitService.initUserDatabase();
    }

    @Test
    public void shouldReturnForbiddenStatusFotNonAdminRole() throws Exception {
        MultiValueMap<String, String> registerParams = new LinkedMultiValueMap<>();

        registerParams.add("userId", "");

        ResultActions resultActions = mockMvc.perform(get(EVENT_CONTROLLER_PATH).params(registerParams)
            .with(getRequestPostProcessorForUser(testUser()))
            .with(csrf()));

        resultActions.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void shouldLoggedInEventByUserLogin() throws Exception {
        MultiValueMap<String, String> registerParams = new LinkedMultiValueMap<>();
        String userLogin = testAdminUser().getLogin();
        registerParams.add("userLogin", userLogin);

        ResultActions resultActions = mockMvc.perform(get(EVENT_CONTROLLER_PATH).params(registerParams)
            .with(getRequestPostProcessorForUser(testAdminUser()))
            .with(csrf()));

        resultActions.andExpect(status().is(HttpStatus.OK.value()));

        String html = resultActions.andReturn().getResponse().getContentAsString();
        Document doc = Jsoup.parse(html);
        Element eventsTable = doc.getElementById("eventsTable");
        Elements trElements = eventsTable.getElementsByTag("tbody").first().getElementsByTag("tr");
        Element eventElement = trElements.get(0);
        Elements elements = eventElement.getElementsByTag("td");
        String actualUserLogin = elements.get(elements.size() - 1).text();
        EventType actualEventType = EventType.valueOf(eventElement.getElementsByTag("td").get(2).text());

        Assertions.assertEquals(userLogin, actualUserLogin);
        Assertions.assertEquals(EventType.LOGGED_IN, actualEventType);
    }

}
