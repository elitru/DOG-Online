package com.tl.models.application.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.tl.models.application.game.states.GameState;
import com.tl.models.application.user.SessionUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class GameSessionContext {
    private GameState state;

    private String sessionName;
    private UUID sessionId;
    private String password;
    private boolean publicSession;
    private Game game;
    private Map<UUID, SessionUser> clients = new HashMap<>();
    private SessionUser owner;
}
