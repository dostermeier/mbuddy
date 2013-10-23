package com.zutubi.services.mail.api;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class MailMessage {

    private UUID id;
    private byte[] data;
    private String envelopeSender;
    private String envelopeReceiver;

    public MailMessage() {
    }

    public MailMessage(String envelopeSender, String envelopeReceiver, byte[] messageData) {
        this.id = UUID.randomUUID();
        this.envelopeSender = envelopeSender;
        this.envelopeReceiver = envelopeReceiver;
        this.data = messageData;
    }

    /**
     * Get the raw message DATA.
     */
    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Get the RCPT TO:
     */
    public String getEnvelopeReceiver() {
        return this.envelopeReceiver;
    }

    public void setEnvelopeReceiver(String envelopeReceiver) {
        this.envelopeReceiver = envelopeReceiver;
    }

    /**
     * Get the MAIL FROM:
     */
    public String getEnvelopeSender() {
        return this.envelopeSender;
    }

    public void setEnvelopeSender(String envelopeSender) {
        this.envelopeSender = envelopeSender;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
