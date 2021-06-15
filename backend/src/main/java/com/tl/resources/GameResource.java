package com.tl.resources;


import com.tl.models.client.requests.SwapCardRequest;
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
    public void nextState(@HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId) {
        this.sessionService.advanceStateForSession(UUID.fromString(sessionId), UUID.fromString(userId));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/swap")
    public void swapCards(@HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId, SwapCardRequest request) {
        var session = this.sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        System.out.println(session.getGame().getState().getClass());
        session.getGame().getState().swapCard(UUID.fromString(userId), request.toPlayer, request.cardId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/available-moves")
    public void availableMoves(@HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId) {
        var session = this.sessionService.getSessionOrThrow(UUID.fromString(sessionId));

    }


}
