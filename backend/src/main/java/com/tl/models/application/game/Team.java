package com.tl.models.application.game;

import com.tl.models.application.user.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Team {
    private final int teamId;
    private List<SessionUser> members;

    private static final int MAX_TEAM_SIZE = 2;

    public boolean isFull() {
        return this.members.size() == MAX_TEAM_SIZE;
    }

    public void removeUserFromTeam(SessionUser user) {
        this.members.remove(user);
    }

    public void addUserToTeam(SessionUser user) {
        this.members.add(user);
    }

    public int remainingCapacity() {
        return MAX_TEAM_SIZE - this.members.size();
    }
}
