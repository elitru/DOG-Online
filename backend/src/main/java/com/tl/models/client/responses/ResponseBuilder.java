package com.tl.models.client.responses;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class ResponseBuilder {
    public static Response build(Object body, UUID sessionId, UUID userId) {
        return Response.ok(body, MediaType.APPLICATION_JSON)
                .expires(Date.from(Instant.now().plus(Duration.ofMinutes(60 * 5))))
                .cookie(new NewCookie("userId", userId.toString()))
                .cookie(new NewCookie("sessionId", sessionId.toString()))
                .build();
    }
}
