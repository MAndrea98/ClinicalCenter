package com.example.demo.controller;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.example.demo.model.Korisnik;

@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Locale locale;
    private final Korisnik user;

    public OnRegistrationCompleteEvent(final Korisnik user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    //

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public Korisnik getUser() {
        return user;
    }

}
