package me.tewpingz.core.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.UUID;

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

        public ProfileSnapshot(Profile profile) {
            this.playerId = profile.getPlayerId();
            this.joinTime = profile.getJoinTime();
            this.lastSeen = profile.getLastSeen();
            this.lastSeenName = profile.getLastSeenName();
        }
    }
}
