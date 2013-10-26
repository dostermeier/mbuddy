package com.zutubi.services.mail.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public final class Account {

    private String email;

    public Account() {

    }

    public Account(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
