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

    public Rank.RankSnapshot getCachedRank(String rankId) {
        return this.collection.getCachedValued(rankId.toLowerCase());
    }

    public Collection<Rank.RankSnapshot> getCachedRanks() {
        return this.collection.getCachedValues();
    }

    public Collection<Rank.RankSnapshot> getCachedSortedRanks() {
        List<Rank.RankSnapshot> rankList = new ArrayList<>(this.getCachedRanks());
        rankList.sort(Rank.RankSnapshot::compareTo);
        return rankList;
    }

    public CompletableFuture<Collection<Rank.RankSnapshot>> getCachedSortedRanksAsync() {
        return CompletableFuture.supplyAsync(this::getCachedSortedRanks);
    }

    public CompletableFuture<Rank.RankSnapshot> getRealRank(String rankId) {
        return this.collection.getOrCreateRealValueAsync(rankId.toLowerCase());
    }

    public CompletableFuture<Rank.RankSnapshot> updateRealRankAsync(String rankId, Consumer<Rank> consumer) {
        return this.collection.updateRealValueAsync(rankId.toLowerCase(), consumer);
    }

    public CompletableFuture<Void> evictRankAsync(String rankId) {
        return this.collection.evictRealValueAsync(rankId.toLowerCase());
    }
}
