package me.tewpingz.core.profile;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProfileManager {

    private final RediGoCollection<Profile.ProfileSnapshot, UUID, Profile> collection;

    public ProfileManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("profile", UUID.class, Profile.class, 30, false, playerId -> {
            String name = Core.getInstance().getUuidManager().getName(playerId).getName();
            Profile profile = new Profile(playerId);
            profile.setLastSeenName(name);
            return profile;
        }, Profile::new);
    }

    public Profile.ProfileSnapshot beginCachingLocally(UUID playerId) {
        this.collection.beginCachingLocally(playerId);
        return this.getCachedValue(playerId);
    }

    public Profile.ProfileSnapshot getCachedValue(UUID playerId) {
        return this.collection.getCachedValued(playerId);
    }

    public Collection<Profile.ProfileSnapshot> getCachedValues() {
        return this.collection.getCachedValues();
    }

    public void forEachCachedValue(Consumer<Profile.ProfileSnapshot> consumer) {
        this.collection.forEachCachedValue(consumer);
    }

    public void stopCachingLocally(UUID playerId) {
        this.collection.stopCachingLocally(playerId);
    }

    public Profile.ProfileSnapshot getRealValue(UUID playerId) {
        return this.collection.getOrCreateRealValue(playerId);
    }

    public CompletableFuture<Profile.ProfileSnapshot> getRealValueAsync(UUID playerId) {
        return this.collection.getOrCreateRealValueAsync(playerId);
    }

    public Profile.ProfileSnapshot updateRealValue(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValue(playerId, consumer);
    }

    public CompletableFuture<Profile.ProfileSnapshot> updateRealValueAsync(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValueAsync(playerId, consumer);
    }
}
