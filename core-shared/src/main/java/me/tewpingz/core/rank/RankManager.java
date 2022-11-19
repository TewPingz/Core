package me.tewpingz.core.rank;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RankManager {

    private final RediGoCollection<Rank.RankSnapshot, String, Rank> collection;

    public RankManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("ranks", String.class, Rank.class, 30, true, rankId -> new Rank(rankId.toLowerCase()));
        this.collection.updateRealValue("default", rank -> {
            if (rank.getDisplayName().isEmpty()) {
                rank.setDisplayName("Default");
            }
        });
        this.collection.beginCachingOrUpdateLocally("default"); // Basically create default if it doesn't exist
    }

    /**
     * A function to get a cached rank
     * @param rankId the rank id
     * @return the cached rank snapshot
     */
    public Rank.RankSnapshot getCachedRank(String rankId) {
        return this.collection.getCachedValued(rankId.toLowerCase());
    }

    /**
     * A function to get all the cached ranks
     * @return a collection of rank snapshots
     */
    public Collection<Rank.RankSnapshot> getCachedRanks() {
        return this.collection.getCachedValues();
    }

    /**
     * A function to get all the ranks sorted by priority
     * @return the collection of rank snapshots sorted by priority
     */
    public Collection<Rank.RankSnapshot> getCachedSortedRanks() {
        List<Rank.RankSnapshot> rankList = new ArrayList<>(this.getCachedRanks());
        rankList.sort(Rank.RankSnapshot::compareTo);
        return rankList;
    }

    /**
     * A function to get all the ranks sorted by priority
     * This function is called asynchronously
     * @return the collection of rank snapshots sorted by priority
     */
    public CompletableFuture<Collection<Rank.RankSnapshot>> getCachedSortedRanksAsync() {
        return CompletableFuture.supplyAsync(this::getCachedSortedRanks);
    }

    /**
     * A function to get the real rank instance
     * This means the latest version of the rank
     * @param rankId the rank id
     * @return a completable future with the rank snapshot
     */
    public CompletableFuture<Rank.RankSnapshot> getRealRankAsync(String rankId) {
        return this.collection.getOrCreateRealValueAsync(rankId.toLowerCase());
    }

    /**
     * A function to update the real rank asynchronously
     * @param rankId the rank id to update
     * @param consumer the consumer that will apply the updates
     * @return a completable future with the rank snapshot
     */
    public CompletableFuture<Rank.RankSnapshot> updateRealRankAsync(String rankId, Consumer<Rank> consumer) {
        return this.collection.updateRealValueAsync(rankId.toLowerCase(), consumer);
    }

    /**
     * A function to remove a rank asynchronously
     * @param rankId the rank id to evict
     * @return a completable future to track the change
     */
    public CompletableFuture<Void> evictRankAsync(String rankId) {
        return this.collection.evictRealValueAsync(rankId.toLowerCase());
    }
}
