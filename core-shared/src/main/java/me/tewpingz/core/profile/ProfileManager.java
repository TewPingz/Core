package me.tewpingz.core.profile;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.Collection;
import java.util.Objects;
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

    /**
     * A function to begin caching or update the currently cached profile
     * This function does call the database, so it is recommended to call this asynchronously.
     *
     * @param playerId the id of the profile
     * @return the cached instance of {@link Profile.ProfileSnapshot} with the id provided
     */
    public Profile.ProfileSnapshot beginCachingOrUpdateProfile(UUID playerId) {
        Objects.requireNonNull(playerId);
        return this.collection.beginCachingOrUpdateLocally(playerId);
    }

    /**
     * A function to begin caching or update the currently cached profile
     * This function is called asynchronously compared to {@link ProfileManager#beginCachingOrUpdateProfile(UUID)}
     *
     * @param playerId the id of the profile
     * @return the cached instance of {@link me.tewpingz.core.profile.Profile.ProfileSnapshot} with the id provided in a {@link CompletableFuture}
     */
    public CompletableFuture<Profile.ProfileSnapshot> beginCachingOrUpdateLocallyAsync(UUID playerId) {
        Objects.requireNonNull(playerId);
        return this.collection.beginCachingOrUpdateLocallyAsync(playerId);
    }

    /**
     * A function to get the cached profile of the id provided
     * @param playerId the id to get the cached profile for
     * @return the cached instance of {@link me.tewpingz.core.profile.Profile.ProfileSnapshot}
     */
    public Profile.ProfileSnapshot getCachedProfile(UUID playerId) {
        return this.collection.getCachedValued(playerId);
    }

    /**
     * A function to get the collection of cached profile
     * @return a collection with all the cached profiles
     */
    public Collection<Profile.ProfileSnapshot> getCachedProfiles() {
        return this.collection.getCachedValues();
    }

    /**
     * A function to stop caching the profile
     * @param playerId the profile id to stop caching
     */
    public void stopCachingProfile(UUID playerId) {
        this.collection.stopCachingLocally(playerId);
    }

    /**
     * A function to get the real value from the database
     * @param playerId the id of the profile
     * @return a {@link me.tewpingz.core.profile.Profile.ProfileSnapshot} of the latest {@link Profile} instance
     */
    public Profile.ProfileSnapshot getRealProfile(UUID playerId) {
        return this.collection.getOrCreateRealValue(playerId);
    }

    /**
     * A function to get the real profile asynchronously
     * @param playerId the id of the profile
     * @return a {@link me.tewpingz.core.profile.Profile.ProfileSnapshot} of the latest {@link Profile} instance in a {@link CompletableFuture}
     */
    public CompletableFuture<Profile.ProfileSnapshot> getRealProfileAsync(UUID playerId) {
        return this.collection.getOrCreateRealValueAsync(playerId);
    }

    /**
     * A function to be able to apply updates to the latest profile
     * @param playerId the player id to apply to update to
     * @param consumer the consumer that is called to apply the update to the profile
     * @return a {@link me.tewpingz.core.profile.Profile.ProfileSnapshot} of the latest {@link Profile} instance
     */
    public Profile.ProfileSnapshot updateRealProfile(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValue(playerId, consumer);
    }

    /**
     * A function to be able to apply updates to the latest profile
     * @param playerId the player id to apply to update to
     * @param consumer the consumer that is called to apply the update to the profile
     * @return a {@link me.tewpingz.core.profile.Profile.ProfileSnapshot} of the latest {@link Profile} instance in a {@link CompletableFuture}
     */
    public CompletableFuture<Profile.ProfileSnapshot> updateRealProfileAsync(UUID playerId, Consumer<Profile> consumer) {
        return this.collection.updateRealValueAsync(playerId, consumer);
    }
}
