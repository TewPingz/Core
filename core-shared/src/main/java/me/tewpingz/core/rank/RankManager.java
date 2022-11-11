package me.tewpingz.core.rank;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class RankManager {

    private final RediGoCollection<Rank.RankSnapshot, String, Rank> collection;

    public RankManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("ranks", String.class, Rank.class, 30, true, rankId -> new Rank(rankId.toLowerCase()));
        this.collection.updateRealValue("default", rank -> {
            if (rank.getDisplayName().isEmpty()) {
                rank.setDisplayName("Default");
            }
        });
        this.collection.beginCachingLocally("default"); // Basically create default if it doesn't exist
    }

    public Rank.RankSnapshot getRank(String rankId) {
        return this.collection.getCachedValued(rankId.toLowerCase());
    }

    public Collection<Rank.RankSnapshot> getRanks() {
        return this.collection.getCachedValues();
    }

    public Collection<Rank.RankSnapshot> getSortedRanks() {
        List<Rank.RankSnapshot> rankList = new ArrayList<>(this.getRanks());
        rankList.sort(Rank.RankSnapshot::compareTo);
        return rankList;
    }

    public CompletableFuture<Collection<Rank.RankSnapshot>> getSortedRanksAsync() {
        return CompletableFuture.supplyAsync(this::getSortedRanks);
    }

    public CompletableFuture<Rank> getRealValueAsync(String rankId) {
        return this.collection.getOrCreateRealValueAsync(rankId.toLowerCase());
    }

    public CompletableFuture<Rank> updateRealValueAsync(String rankId, Consumer<Rank> consumer) {
        return this.collection.updateRealValueAsync(rankId.toLowerCase(), consumer);
    }

    public <T> CompletableFuture<T> updateRealValueWithFunctionAsync(String rankId, Function<Rank, T> function) {
        return this.collection.updateRealValueWithFunctionAsync(rankId.toLowerCase(), function);
    }
}
