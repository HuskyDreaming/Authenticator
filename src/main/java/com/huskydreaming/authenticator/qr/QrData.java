package com.huskydreaming.authenticator.qr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QrData {

    private final String title;
    private final String description;
    private final String secret;

    public QrData(String title, String description, String secret) {
        this.title = title;
        this.description = description;
        this.secret = secret;
    }

    public String getUri() {
        return "otpauth://" +
                encode("totp") + "/" +
                encode(description) + "?" +
                "secret=" + encode(secret) +
                "&issuer=" + encode(title) +
                "&algorithm=" + encode("SHA1") +
                "&digits=" + 6 +
                "&period=" + 30;
    }

    private String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
