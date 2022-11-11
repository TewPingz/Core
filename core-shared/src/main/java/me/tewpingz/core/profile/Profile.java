package me.tewpingz.core.profile;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.rank.grant.RankGrant;
import me.tewpingz.core.rank.grant.event.RankGrantCreateEvent;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.*;

@Data
@RequiredArgsConstructor
public class Profile implements RediGoObject<UUID, Profile.ProfileSnapshot> {

    private final UUID playerId;

    @RediGoValue(key = "joinTime")
    private long joinTime;

    @RediGoValue(key = "lastSeen")
    private long lastSeen;

    @RediGoValue(key = "lastSeenName")
    private String lastSeenName;

    @RediGoValue(key = "activeGrants")
    private Set<RankGrant> activeGrants = new HashSet<>();

    @RediGoValue(key = "expiredGrants")
    private Set<RankGrant.ExpiredRankGrant> expiredGrants = new HashSet<>();

    public boolean addGrant(Rank rank, String executor, String reason, long duration) {
        return this.addGrant(rank.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(Rank.RankSnapshot rankSnapshot, String executor, String reason, long duration) {
        return this.addGrant(rankSnapshot.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(String rankId, String executor, String reason, long duration) {
        RankGrant grant = new RankGrant(rankId, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new RankGrantCreateEvent(executor, this.playerId, grant));
        return this.activeGrants.add(grant);
    }

    public boolean removeGrant(RankGrant grant, String removedBy, String removedFor) {
        if (this.activeGrants.remove(grant)) {
            return this.expiredGrants.add(new RankGrant.ExpiredRankGrant(grant, removedBy, removedFor, System.currentTimeMillis()));
        }
        return false;
    }

    public RankGrant getDisplayGrant() {
        RankGrant current = null;

        for (RankGrant activeGrant : this.activeGrants) {
            if (activeGrant.getRankSnapshot() == null) {
                continue;
            }

            if (current == null || (current.getRankSnapshot().getPriority() < activeGrant.getRankSnapshot().getPriority())) {
                current = activeGrant;
            }
        }

        return current;
    }

    public Rank.RankSnapshot getDisplayRank() {
        RankGrant rankGrant = this.getDisplayGrant();

        if (rankGrant == null) {
            return Core.getInstance().getRankManager().getRank("default");
        }

        return rankGrant.getRankSnapshot();
    }

    @Override
    public UUID getKey() {
        return this.playerId;
    }

    @Override
    public ProfileSnapshot getSnapshot() {
        return new ProfileSnapshot(this);
    }

    @Getter
    public static class ProfileSnapshot implements Snapshot {
        private final UUID playerId;
        private final long joinTime, lastSeen;
        private final String lastSeenName;
        private final Set<RankGrant> activeGrants;
        private final Set<RankGrant.ExpiredRankGrant> expiredGrants;
        private final String displayRankId;

        public ProfileSnapshot(Profile profile) {
            this.playerId = profile.getPlayerId();
            this.joinTime = profile.getJoinTime();
            this.lastSeen = profile.getLastSeen();
            this.lastSeenName = profile.getLastSeenName();
            this.activeGrants = profile.getActiveGrants();
            this.expiredGrants = profile.getExpiredGrants();
            this.displayRankId = profile.getDisplayRank().getRankId();
        }

        public Rank.RankSnapshot getDisplayRank() {
            Rank.RankSnapshot displayRank = Core.getInstance().getRankManager().getRank(this.displayRankId);

            if (displayRank == null) {
                return Core.getInstance().getRankManager().getRank("default");
            }

            return displayRank;
        }
    }
}
