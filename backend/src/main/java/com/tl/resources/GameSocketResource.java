package com.tl.resources;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.MessageDecoder;
import com.tl.models.application.game.ws_messages.MessageEncoder;
import com.tl.models.application.game.ws_messages.messages.UserUpdateMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.models.application.user.User;
import com.tl.services.SessionService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@ServerEndpoint(
        value = "/game/{sessionId}/{userId}",
        encoders = {
                MessageEncoder.class
        },
        decoders = {
                MessageDecoder.class
        }
)
@ApplicationScoped
public class GameSocketResource {

    @Inject
    SessionService sessionService;

    public static String getUrlForUserAndSession(UUID sessionId, UUID userId) {
        return String.format("ws://localhost:8080/game/%s/%s", sessionId, userId);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId, @PathParam("userId") String userId) {
        var context = sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        var user = context.getClients().get(UUID.fromString(userId));
        System.out.println("here -> " + user);
        System.out.println("here -> " + context);

        if(user == null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // add client session to game user
        user.setWebsocketSession(session);
        // send message to all users that a new user has joined
        makeGameBroadcast(context, new UserUpdateMessage(context.getClients().values().stream().map(SessionUser::toBaseUser).collect(Collectors.toList())));
    }

    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String sessionId, @PathParam("userId") String userId) {
            System.out.println("close");
        var context = sessionService.getSessionOrThrow(UUID.fromString(sessionId));
        sessionService.quitSession(UUID.fromString(sessionId), UUID.fromString(userId));
        makeGameBroadcast(context, new UserUpdateMessage(new ArrayList<>(context.getClients().values())));
    }

    @OnError
    public void onError(Session session, @PathParam("sessionId") String sessionId, @PathParam("userId") String userId, Throwable throwable) {
        System.out.println("hello from error fuck");
        throwable.printStackTrace();
        this.onClose(session, sessionId, userId);
    }

    @OnMessage
    public void onMessage(Message message, @PathParam("sessionId") String sessionId, @PathParam("userId") String userId) {
        System.out.println("new message: " + message.toString());
    }

    public static void makeGameBroadcast(GameSessionContext context, Message message) {
        context.getClients()
                .values()
                .stream()
                .map(SessionUser::getWebsocketSession)
                .forEach(session -> {
                    if(session == null) {
                       return;
                    }
                    session.getAsyncRemote().sendObject(message);
                });
    }
}