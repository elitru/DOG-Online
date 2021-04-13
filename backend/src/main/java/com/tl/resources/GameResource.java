package com.tl.resources;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/game")
public class GameResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next")
    public void nextState(@CookieParam("sessionId") String sessionId, @CookieParam("userId") String userId) {

    }
}
