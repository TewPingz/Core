package me.tewpingz.core.profile;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProfileManager {

    private final RediGoCollection<UUID, Profile> collection;

    public ProfileManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("profile", UUID.class, Profile.class, 30, false, playerId -> {
            Profile profile = new Profile(playerId);
            profile.setJoinTime(System.currentTimeMillis());
            return profile;
        });
    }

    public Profile beginCachingLocally(UUID playerId) {
        this.collection.beginCachingLocally(playerId);
        return this.getCachedValue(playerId);
    }

    public Profile getCachedValue(UUID playerId) {
        return this.collection.getCachedValued(playerId);
    }

    public void stopCachingLocally(UUID playerId) {
        this.collection.stopCachingLocally(playerId);
    }

    public CompletableFuture<Profile> getRealValueAsync(UUID playerId) {
        return this.collection.getOrCreateRealValueAsync(playerId);
    }

    public CompletableFuture<Void> updateRealValueAsync(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValueAsync(playerId, consumer);
    }
}
