package com.tl.resources;


import com.tl.models.client.requests.MakeMoveRequest;
import com.tl.models.client.requests.PlayCardRequest;
import com.tl.models.client.requests.SwapCardRequest;
import com.tl.models.client.responses.MoveResponse;
import com.tl.services.SessionService;
import com.tl.validation.Validation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
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
        Validation.checkForNull(request);
        var session = this.sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        System.out.println(session.getGame().getState().getClass());
        session.getGame().getState().swapCard(UUID.fromString(userId), request.toPlayer, request.cardId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/available-moves")
    public MoveResponse availableMoves(@HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId, MakeMoveRequest request) {
        Validation.checkForNull(request);
        var session = this.sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        var positions = session.getState().calculateAllMoves(request.pinId, request.cardId, Optional.empty(),
                session.getClients().get(UUID.fromString(userId)));
        return new MoveResponse(positions);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/play-card")
    public void playCard(@HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId, PlayCardRequest request) {
        var session = this.sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        session.getState().playCard(request, session.getClients().get(UUID.fromString(userId)));
    }


}
