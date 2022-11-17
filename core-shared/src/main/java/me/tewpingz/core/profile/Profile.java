package me.tewpingz.core.profile;

import lombok.AccessLevel;
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

    // Metrics
    @RediGoValue(key = "joinTime")
    private long joinTime = -1;
    @RediGoValue(key = "lastSeen")
    private long lastSeen = -1;

    // Information
    @RediGoValue(key = "lastSeenName")
    private String lastSeenName;
    @RediGoValue(key = "lastIp")
    private String lastIp;

    // Grants
    @RediGoValue(key = "activeGrants")
    private Set<Grant> activeGrants = new HashSet<>();
    @RediGoValue(key = "expiredGrants")
    private Set<Grant.ExpiredGrant> expiredGrants = new HashSet<>();

    // Punishments
    @RediGoValue(key = "activePunishments")
    private Set<Punishment> activePunishments = new HashSet<>();
    @RediGoValue(key = "expiredPunishments")
    private Set<Punishment.ExpiredPunishment> expiredPunishments = new HashSet<>();

    // Tracking
    @RediGoValue(key = "reportCooldown")
    private long reportCooldown = -1;
    @RediGoValue(key = "requestCooldown")
    private long requestCooldown = -1;

    // Discord sync
    @RediGoValue(key = "syncCode")
    private String syncCode;
    @RediGoValue(key = "syncCooldown")
    private long syncCooldown = -1;
    @RediGoValue(key = "discordId")
    private String discordId;

    /**
     * A function to add a grant to the profile
     * @param rank an instance of {@link me.tewpingz.core.rank.Rank.RankSnapshot} for the rank of the grant
     * @param executor the executor providing the grant
     * @param reason the reason this grant is being provided
     * @param duration the duration of the grant
     * @return whether the grant has been applied or not
     */
    public boolean addGrant(Rank.RankSnapshot rank, String executor, String reason, long duration) {
        return this.addGrant(rank.getRankId(), executor, reason, duration);
    }

    /**
     * A function to add a grant to the profile
     * @param rankId the rank id of the grant being applied.
     * @param executor the executor providing the grant
     * @param reason the reason this grant is being provided
     * @param duration the duration of the grant
     * @return whether the grant has been applied or not
     */
    public boolean addGrant(String rankId, String executor, String reason, long duration) {
        Grant grant = new Grant(rankId, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new GrantAddEvent(executor, this.playerId, grant));
        return this.activeGrants.add(grant);
    }

    /**
     * A function to remove the grant that is being provided
     * @param grant the grant to remove
     * @param removedBy who the grant was removed by
     * @param removedFor why the grant has been removed
     * @return whether the grant has been removed or not.
     */
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

    /**
     * A function to add a punishment to the profile
     * @param punishmentType the punishment type being added
     * @param executor the executor of the punishment
     * @param reason the reason why the punishment is being added
     * @param duration the duration of the punishment
     * @return whether the punishment was successfully added or not.
     */
    public boolean addPunishment(PunishmentType punishmentType, String executor, String reason, long duration) {
        Punishment punishment = new Punishment(punishmentType, executor, reason, System.currentTimeMillis(), duration);
        Core.getInstance().getBridge().callEvent(new PunishmentAddEvent(executor, this.playerId, punishment));
        return this.activePunishments.add(punishment);
    }

    /**
     * A function to remove a punishment from the account
     * @param punishment the punishment that is being removed
     * @param removedBy the person who is removing the punishments
     * @param removedFor the reason it was removed.
     * @return whether the punishment has been removed or not.
     */
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

    /**
     * A function to get the ban on the account
     * @return an optional of the punishment instance
     */
    public Optional<Punishment> getBan() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).findFirst();
    }

    /**
     * A function to get the mute on the account
     * @return an optional of the punishment instance
     */
    public Optional<Punishment> getMute() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).findFirst();
    }

    /**
     * A function to get the blacklist on the account
     * @return an optional of the punishment instance
     */
    public Optional<Punishment> getBlacklist() {
        return this.activePunishments.stream().filter(punishment -> !punishment.hasExpired())
                .filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst();
    }

    /**
     * A function to get the sorted active grants
     * @return a list of grants that is sorted by their start time.
     */
    public List<Grant> getSortedActiveGrants() {
        List<Grant> activeGrants = new ArrayList<>(this.activeGrants);
        activeGrants.sort(Comparator.comparingLong(Grant::getStartTimestamp));
        return activeGrants;
    }

    /**
     * A function to get the expired grants sorted by their start time
     * @return a list of expired grants that are sorted by their start time.
     */
    public List<Grant.ExpiredGrant> getSortedExpiredGrants() {
        List<Grant.ExpiredGrant> expiredGrants = new ArrayList<>(this.expiredGrants);
        expiredGrants.sort(Comparator.comparingLong(value -> value.getGrant().getStartTimestamp()));
        return expiredGrants;
    }

    /**
     * A function to get the sorted active punishments
     * @return a list of punishments that is sorted by their start time.
     */
    public List<Punishment> getSortedActivePunishments() {
        List<Punishment> punishments = new ArrayList<>(this.activePunishments);
        punishments.sort(Comparator.comparingLong(Punishment::getStartTimestamp));
        return punishments;
    }

    /**
     * A function to get the expired punishments sorted by their start time
     * @return a list of expired punishments that are sorted by their start time.
     */
    public List<Punishment.ExpiredPunishment> getSortedExpiredPunishments() {
        List<Punishment.ExpiredPunishment> expiredGrants = new ArrayList<>(this.expiredPunishments);
        expiredGrants.sort(Comparator.comparingLong(value -> value.getPunishment().getStartTimestamp()));
        return expiredGrants;
    }

    /**
     * A function that allows you to get the highest weight rank to display
     * @return the highest display grant.
     */
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

    /**
     * A function to get the display rank of the player
     * @return the most weighted rank that can be shown on the player.
     */
    public Rank.RankSnapshot getDisplayRank() {
        Grant grant = this.getDisplayGrant();

        if (grant == null) {
            return Core.getInstance().getRankManager().getCachedRank("default");
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
        private final String lastIp, lastSeenName, discordId, syncCode;

        private final List<Grant> activeGrants;
        private final List<Grant.ExpiredGrant> expiredGrants;
        private final List<Punishment> activePunishments;
        private final List<Punishment.ExpiredPunishment> expiredPunishments;

        private final Punishment ban;
        private final Punishment mute;
        private final Punishment blacklist;

        @Getter(AccessLevel.NONE)
        private final String displayRankId;

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
            this.activeGrants = List.copyOf(profile.getSortedActiveGrants());
            this.expiredGrants = List.copyOf(profile.getSortedExpiredGrants());
            this.activePunishments = List.copyOf(profile.getSortedActivePunishments());
            this.expiredPunishments = List.copyOf(profile.getSortedExpiredPunishments());
            this.ban = profile.getBan().orElse(null);
            this.mute = profile.getMute().orElse(null);
            this.blacklist = profile.getBlacklist().orElse(null);
        }

        public Rank.RankSnapshot getDisplayRank() {
            Rank.RankSnapshot displayRank = Core.getInstance().getRankManager().getCachedRank(this.displayRankId);

            if (displayRank == null) {
                return Core.getInstance().getRankManager().getCachedRank("default");
            }

            return displayRank;
        }
    }
}
