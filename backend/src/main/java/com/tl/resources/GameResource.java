package com.tl.resources;


import com.tl.services.SessionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/game")
public class GameResource {

    @Inject
    SessionService sessionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next")
    public void nextState(@CookieParam("sessionId") String sessionId, @CookieParam("userId") String userId) {
        this.sessionService.advanceStateForSession(UUID.fromString(sessionId), UUID.fromString(userId));
    }
}
