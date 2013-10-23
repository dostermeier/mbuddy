package com.zutubi.services.mail.system.utils;

import org.springframework.mail.SimpleMailMessage;

/**
 *
 */
public final class SimpleMailMessageBuilder {

    private final SimpleMailMessage message;

    private SimpleMailMessageBuilder() {
        this.message = new SimpleMailMessage();
    }

    public static SimpleMailMessageBuilder simpleMailMessage() {
        return new SimpleMailMessageBuilder();
    }

    public SimpleMailMessageBuilder setTo(String to) {
        this.message.setTo(to);
        return this;
    }

    public SimpleMailMessageBuilder setFrom(String from) {
        this.message.setFrom(from);
        return this;
    }

    public SimpleMailMessageBuilder setText(String text) {
        this.message.setText(text);
        return this;
    }

    public SimpleMailMessage build() {
        return message;
    }
}
