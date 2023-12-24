package com.github.vssavin.usman_webstatic.spring5.email;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vssavin on 24.12.2023.
 */
@Service
@Primary
class MockedEmailServiceImpl implements MockedEmailService {

    private final List<EmailMessage> messages = new ArrayList<>();

    @Override
    public void sendSimpleMessage(String destinationEmail, String subject, String text) {
        messages.add(new EmailMessage(destinationEmail, subject, text));
    }

    public List<EmailMessage> getEmailMessages() {
        return messages;
    }

    public EmailMessage getLastEmailMessage() {
        if (messages.size() == 0) {
            throw new IllegalStateException("Emails list is empty!");
        }
        return messages.get(messages.size() - 1);
    }

}
