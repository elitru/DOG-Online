package com.tl.models.application.game;

import com.tl.models.application.user.SessionUser;

import java.util.List;
import java.util.Map;

public class Game {
    private Map<SessionUser, List<NinePin>> ninepins;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 8;
}
