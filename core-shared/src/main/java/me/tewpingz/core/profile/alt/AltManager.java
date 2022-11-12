package me.tewpingz.core.profile.alt;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.UUID;

public class AltManager {

    private final RediGoCollection<AltEntry.AltProfileSnapshot, String, AltEntry> collection;

    public AltManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("alts", String.class, AltEntry.class, 30, false, AltEntry::new);
    }

    public AltEntry addUuid(String hashedIp, UUID uuid) {
        return this.collection.updateRealValue(hashedIp, altEntry -> altEntry.addUuid(uuid));
    }

    public AltEntry.AltProfileSnapshot getAlts(String hashedIp) {
        return this.collection.getOrCreateRealValue(hashedIp);
    }
}
