package com.tl.resources;

import com.tl.models.client.requests.CreateSessionRequest;
import com.tl.models.client.responses.CreateSessionResponse;
import com.tl.services.SessionService;
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
    public CreateSessionResponse createSession(CreateSessionRequest request) {
        var response = this.sessionService.createSession(request);
        return new CreateSessionResponse(response.getSessionId(), response.getOwner().getId());
    }
}
