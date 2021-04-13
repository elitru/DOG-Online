package com.tl.resources;

import com.tl.models.client.requests.JoinTeamRequest;
import com.tl.validation.Validation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/teams")
public class TeamResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/join")
    public void joinTeam(JoinTeamRequest request) {
        Validation.checkForNull(request);
    }
}
