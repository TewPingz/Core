package me.tewpingz.core.profile;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.profile.grant.Grant;
import me.tewpingz.core.profile.grant.event.GrantCreateEvent;
import me.tewpingz.core.profile.grant.event.GrantRemoveEvent;
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
    private Set<Grant> activeGrants = new HashSet<>();

    @RediGoValue(key = "expiredGrants")
    private Set<Grant.ExpiredGrant> expiredGrants = new HashSet<>();

    public boolean addGrant(Rank rank, String executor, String reason, long duration) {
        return this.addGrant(rank.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(Rank.RankSnapshot rankSnapshot, String executor, String reason, long duration) {
        return this.addGrant(rankSnapshot.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(String rankId, String executor, String reason, long duration) {
        Grant grant = new Grant(rankId, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new GrantCreateEvent(executor, this.playerId, grant));
        return this.activeGrants.add(grant);
    }

    public boolean removeGrant(Grant grant, String removedBy, String removedFor) {
        if (this.activeGrants.remove(grant)) {
            Grant.ExpiredGrant removedGrant = new Grant.ExpiredGrant(grant, removedBy, removedFor, System.currentTimeMillis());
            if (this.expiredGrants.add(removedGrant)) {
                Core.getInstance().getBridge().callEvent(new GrantRemoveEvent(removedBy, this.playerId, removedGrant));
                return true;
            }
        }
        return false;
    }

    public List<Grant> getSortedActiveGrants() {
        List<Grant> activeGrants = new ArrayList<>(this.activeGrants);
        activeGrants.sort(Comparator.comparingLong(Grant::getStartTimestamp));
        return activeGrants;
    }

    public List<Grant.ExpiredGrant> getExpiredActiveGrants() {
        List<Grant.ExpiredGrant> expiredGrants = new ArrayList<>(this.expiredGrants);
        expiredGrants.sort(Comparator.comparingLong(value -> value.getGrant().getStartTimestamp()));
        return expiredGrants;
    }

    public Grant getDisplayGrant() {
        Grant current = null;

        for (Grant activeGrant : this.activeGrants) {
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
        Grant grant = this.getDisplayGrant();

        if (grant == null) {
            return Core.getInstance().getRankManager().getRank("default");
        }

        return grant.getRankSnapshot();
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
        private final Set<Grant> activeGrants;
        private final Set<Grant.ExpiredGrant> expiredGrants;
        private final List<Grant> sortedActiveGrants;
        private final List<Grant.ExpiredGrant> sortedExpiredGrants;
        private final String displayRankId;

        public ProfileSnapshot(Profile profile) {
            this.playerId = profile.getPlayerId();
            this.joinTime = profile.getJoinTime();
            this.lastSeen = profile.getLastSeen();
            this.lastSeenName = profile.getLastSeenName();
            this.activeGrants = profile.getActiveGrants();
            this.expiredGrants = profile.getExpiredGrants();
            this.displayRankId = profile.getDisplayRank().getRankId();
            this.sortedActiveGrants = profile.getSortedActiveGrants();
            this.sortedExpiredGrants = profile.getExpiredActiveGrants();
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
