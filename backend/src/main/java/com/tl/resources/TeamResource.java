package com.tl.resources;

import com.tl.models.client.requests.JoinTeamRequest;
import com.tl.services.SessionService;
import com.tl.validation.Validation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/teams")
public class TeamResource {

    @Inject
    SessionService sessionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/join")
    public void joinTeam(JoinTeamRequest request, @HeaderParam("sessionId") String sessionId, @HeaderParam("userId") String userId) {
        Validation.checkForNull(request);
        sessionService.joinTeam(UUID.fromString(sessionId), UUID.fromString(userId), request.teamId);
    }
}
