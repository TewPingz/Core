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

    public Profile.ProfileSnapshot beginCachingOrUpdateProfile(UUID playerId) {
        return this.collection.beginCachingOrUpdateLocally(playerId);
    }

    public CompletableFuture<Profile.ProfileSnapshot> beginCachingOrUpdateLocallyAsync(UUID playerId) {
        return this.collection.beginCachingOrUpdateLocallyAsync(playerId);
    }

    public Profile.ProfileSnapshot getCachedProfile(UUID playerId) {
        return this.collection.getCachedValued(playerId);
    }

    public Collection<Profile.ProfileSnapshot> getCachedProfiles() {
        return this.collection.getCachedValues();
    }

    public void forEachCachedProfile(Consumer<Profile.ProfileSnapshot> consumer) {
        this.collection.forEachCachedValue(consumer);
    }

    public void stopCachingProfile(UUID playerId) {
        this.collection.stopCachingLocally(playerId);
    }

    public Profile.ProfileSnapshot getRealProfile(UUID playerId) {
        return this.collection.getOrCreateRealValue(playerId);
    }

    public CompletableFuture<Profile.ProfileSnapshot> getRealProfileAsync(UUID playerId) {
        return this.collection.getOrCreateRealValueAsync(playerId);
    }

    public Profile.ProfileSnapshot updateRealProfile(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValue(playerId, consumer);
    }

    public CompletableFuture<Profile.ProfileSnapshot> updateRealProfileAsync(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValueAsync(playerId, consumer);
    }
}
