package me.tewpingz.core.profile.alt;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class AltEntry implements RediGoObject<String, AltEntry.AltProfileSnapshot> {

    private final String hashedIp;

    @RediGoValue(key = "relatedIds")
    private Set<UUID> relatedIds = new HashSet<>();

    public void addUuid(UUID uuid) {
        this.relatedIds.add(uuid);
    }

    @Override
    public String getKey() {
        return this.hashedIp;
    }

    @Override
    public AltProfileSnapshot getSnapshot() {
        return new AltProfileSnapshot(this.hashedIp, this.relatedIds);
    }

    @Data
    @RequiredArgsConstructor
    public static class AltProfileSnapshot implements Snapshot {
        private final String hashedIp;
        private final Set<UUID> relatedIds;
    }
}
