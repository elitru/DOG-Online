package com.tl.models.client.responses;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;
import static javax.ws.rs.core.NewCookie.DEFAULT_MAX_AGE;

public class ResponseBuilder {
    public static Response build(Object body, UUID sessionId, UUID userId) {
        return Response.ok(body, MediaType.APPLICATION_JSON)
                .expires(Date.from(Instant.now().plus(Duration.ofMinutes(60 * 5))))
                .cookie(newCookie("userId", userId.toString()))
                .cookie(newCookie("sessionId", sessionId.toString()))
                .build();
    }

    private static NewCookie newCookie(String name, String value) {
        return new NewCookie(name, value, "/", null, DEFAULT_VERSION, null, DEFAULT_MAX_AGE, null, false, false);
    }
}
