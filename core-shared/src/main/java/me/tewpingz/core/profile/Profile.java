package me.tewpingz.core.profile;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Profile implements RediGoObject<UUID> {

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
}
