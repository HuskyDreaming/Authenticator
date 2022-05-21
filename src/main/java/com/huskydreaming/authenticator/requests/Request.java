package com.huskydreaming.authenticator.requests;

import com.huskydreaming.authenticator.authentication.Authentication;

public class Request {

    private final RequestType type;
    private final Authentication authentication;

    public static Request create(RequestType type) {
        return new Request(type);
    }

    public Request(RequestType type) {
        this.type = type;
        this.authentication = Authentication.create();
    }

    public Request(RequestType type, Authentication authentication) {
        this.type = type;
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public RequestType getAuthenticationType() {
        return type;
    }
}
