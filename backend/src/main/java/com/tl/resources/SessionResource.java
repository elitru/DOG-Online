package com.tl.resources;

import com.tl.models.client.requests.CreateSessionRequest;
import com.tl.models.client.requests.JoinSessionRequest;
import com.tl.models.client.responses.JoinSessionResponse;
import com.tl.services.SessionService;
import com.tl.validation.Validation;
import io.smallrye.common.annotation.Blocking;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sessions")
public class SessionResource {

    @Inject
    SessionService sessionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    @Path("/create")
    public JoinSessionResponse createSession(CreateSessionRequest request) {
        Validation.checkForNull(request);
        var response = this.sessionService.createSession(request);
        return new JoinSessionResponse(response.getSessionId(), response.getOwner().getId());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    @Path("/join")
    public JoinSessionResponse createSession(JoinSessionRequest request) {
        Validation.checkForNull(request);
        var response = this.sessionService.joinSession(request);
        return new JoinSessionResponse(response.getSessionId(), response.getOwner().getId());
    }
}
