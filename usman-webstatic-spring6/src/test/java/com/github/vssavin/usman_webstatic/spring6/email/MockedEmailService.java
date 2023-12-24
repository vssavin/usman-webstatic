package com.github.vssavin.usman_webstatic.spring6.email;

import com.github.vssavin.usmancore.email.EmailService;

import java.util.List;

/**
 * @author vssavin on 24.12.2023.
 */
public interface MockedEmailService extends EmailService {

    List<EmailMessage> getEmailMessages();

    EmailMessage getLastEmailMessage();

    class EmailMessage {

        private final String destination;

        private final String subject;

        private final String text;

        public EmailMessage(String destination, String subject, String text) {
            this.destination = destination;
            this.subject = subject;
            this.text = text;
        }

        public String getDestination() {
            return destination;
        }

        public String getSubject() {
            return subject;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "EmailMessage{" + "destination='" + destination + '\'' + ", subject='" + subject + '\'' + ", text='"
                    + text + '\'' + '}';
        }

    }

}
