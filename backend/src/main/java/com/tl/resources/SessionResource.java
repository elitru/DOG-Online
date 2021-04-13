package com.tl.resources;

import com.tl.models.client.requests.CreateSessionRequest;
import com.tl.models.client.requests.JoinSessionRequest;
import com.tl.models.client.responses.JoinSessionResponse;
import com.tl.models.client.responses.ResponseBuilder;
import com.tl.services.SessionService;
import com.tl.validation.Validation;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sessions")
public class SessionResource {

    @Inject
    SessionService sessionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createSession(CreateSessionRequest request) {
        Validation.checkForNull(request);
        var session = this.sessionService.createSession(request);

        var body = new JoinSessionResponse(GameSocketResource.getUrlForUserAndSession(session.getSessionId(), session.getOwner().getId()));
        return ResponseBuilder.build(body, session.getSessionId(), session.getOwner().getId());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/join")
    public Response joinSession(JoinSessionRequest request) {
        Validation.checkForNull(request);
        var sessionUser = this.sessionService.joinSession(request);
        var body = new JoinSessionResponse(GameSocketResource.getUrlForUserAndSession(request.getSessionId(), sessionUser.getId()));
        return ResponseBuilder.build(body, request.getSessionId(), sessionUser.getId());
    }

}
