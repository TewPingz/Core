package me.tewpingz.core.profile;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.grant.Grant;
import me.tewpingz.core.profile.grant.event.GrantAddEvent;
import me.tewpingz.core.profile.grant.event.GrantRemoveEvent;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.profile.punishment.event.PunishmentAddEvent;
import me.tewpingz.core.profile.punishment.event.PunishmentRemoveEvent;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.*;

@Data
@RequiredArgsConstructor
public class Profile implements RediGoObject<UUID, Profile.ProfileSnapshot> {

    private final UUID playerId;

    @RediGoValue(key = "joinTime")
    private long joinTime = -1;

    @RediGoValue(key = "lastSeen")
    private long lastSeen = -1;

    @RediGoValue(key = "lastSeenName")
    private String lastSeenName;

    @RediGoValue(key = "lastIp")
    private String lastIp;

    @RediGoValue(key = "activeGrants")
    private Set<Grant> activeGrants = new HashSet<>();

    @RediGoValue(key = "expiredGrants")
    private Set<Grant.ExpiredGrant> expiredGrants = new HashSet<>();

    @RediGoValue(key = "activePunishments")
    private Set<Punishment> activePunishments = new HashSet<>();

    @RediGoValue(key = "expiredPunishments")
    private Set<Punishment.ExpiredPunishment> expiredPunishments = new HashSet<>();

    @RediGoValue(key = "reportCooldown")
    private long reportCooldown = -1;

    @RediGoValue(key = "requestCooldown")
    private long requestCooldown = -1;

    @RediGoValue(key = "syncCode")
    private String syncCode;

    @RediGoValue(key = "syncCooldown")
    private long syncCooldown;

    @RediGoValue(key = "discordId")
    private String discordId;

    public boolean addGrant(Rank rank, String executor, String reason, long duration) {
        return this.addGrant(rank.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(Rank.RankSnapshot rankSnapshot, String executor, String reason, long duration) {
        return this.addGrant(rankSnapshot.getRankId(), executor, reason, duration);
    }

    public boolean addGrant(String rankId, String executor, String reason, long duration) {
        Grant grant = new Grant(rankId, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new GrantAddEvent(executor, this.playerId, grant));
        return this.activeGrants.add(grant);
    }

    public boolean removeGrant(Grant grant, String removedBy, String removedFor) {
        if (this.activeGrants.remove(grant)) {
            Grant.ExpiredGrant expiredGrant = new Grant.ExpiredGrant(grant, removedBy, removedFor, System.currentTimeMillis());
            if (this.expiredGrants.add(expiredGrant)) {
                Core.getInstance().getBridge().callEvent(new GrantRemoveEvent(removedBy, this.playerId, expiredGrant));
                return true;
            }
        }
        return false;
    }

    public boolean addPunishment(PunishmentType punishmentType, String executor, String reason, long duration) {
        Punishment punishment = new Punishment(punishmentType, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new PunishmentAddEvent(executor, this.playerId, punishment));
        return this.activePunishments.add(punishment);
    }

    public boolean removePunishment(Punishment punishment, String removedBy, String removedFor) {
        if (this.activePunishments.remove(punishment)) {
            Punishment.ExpiredPunishment expiredPunishment = new Punishment.ExpiredPunishment(punishment, removedBy, removedFor, System.currentTimeMillis());
            if (this.expiredPunishments.add(expiredPunishment)) {
                Core.getInstance().getBridge().callEvent(new PunishmentRemoveEvent(removedBy, this.playerId, expiredPunishment));
                return true;
            }
        }
        return false;
    }

    public Optional<Punishment> getBan() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).findFirst();
    }

    public Optional<Punishment> getMute() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).findFirst();
    }

    public Optional<Punishment> getBlacklist() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst();
    }

    public List<Grant> getSortedActiveGrants() {
        List<Grant> activeGrants = new ArrayList<>(this.activeGrants);
        activeGrants.sort(Comparator.comparingLong(Grant::getStartTimestamp));
        return activeGrants;
    }

    public List<Grant.ExpiredGrant> getSortedExpiredGrants() {
        List<Grant.ExpiredGrant> expiredGrants = new ArrayList<>(this.expiredGrants);
        expiredGrants.sort(Comparator.comparingLong(value -> value.getGrant().getStartTimestamp()));
        return expiredGrants;
    }

    public List<Punishment> getSortedActivePunishments() {
        List<Punishment> punishments = new ArrayList<>(this.activePunishments);
        punishments.sort(Comparator.comparingLong(Punishment::getStartTimestamp));
        return punishments;
    }

    public List<Punishment.ExpiredPunishment> getSortedExpiredPunishments() {
        List<Punishment.ExpiredPunishment> expiredGrants = new ArrayList<>(this.expiredPunishments);
        expiredGrants.sort(Comparator.comparingLong(value -> value.getPunishment().getStartTimestamp()));
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
        private final long joinTime, lastSeen, requestCooldown, reportCooldown, syncCooldown;
        private final String lastIp, lastSeenName, displayRankId, discordId, syncCode;

        private final List<Grant> sortedActiveGrants;
        private final List<Grant.ExpiredGrant> sortedExpiredGrants;

        private final List<Punishment> sortedActivePunishments;
        private final List<Punishment.ExpiredPunishment> sortedExpiredPunishments;

        private final Punishment ban;
        private final Punishment mute;
        private final Punishment blacklist;

        public ProfileSnapshot(Profile profile) {
            this.playerId = profile.getPlayerId();
            this.joinTime = profile.getJoinTime();
            this.lastIp = profile.getLastIp();
            this.lastSeen = profile.getLastSeen();
            this.reportCooldown = profile.getReportCooldown();
            this.requestCooldown = profile.getRequestCooldown();
            this.syncCooldown = profile.getSyncCooldown();
            this.syncCode = profile.getSyncCode();
            this.discordId = profile.getDiscordId();
            this.lastSeenName = profile.getLastSeenName();
            this.displayRankId = profile.getDisplayRank().getRankId();
            this.sortedActiveGrants = List.copyOf(profile.getSortedActiveGrants());
            this.sortedExpiredGrants = List.copyOf(profile.getSortedExpiredGrants());
            this.sortedActivePunishments = List.copyOf(profile.getSortedActivePunishments());
            this.sortedExpiredPunishments = List.copyOf(profile.getSortedExpiredPunishments());
            this.ban = profile.getBan().orElse(null);
            this.mute = profile.getMute().orElse(null);
            this.blacklist = profile.getBlacklist().orElse(null);
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
