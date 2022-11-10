package me.tewpingz.core.rank;

import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.redigo.RediGoCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

public class RankManager {

    private final RediGoCollection<String, Rank> collection;

    public RankManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("ranks", String.class, Rank.class, 30, true,
                rankName -> new Rank(rankName.toLowerCase(), rankName));
    }

    public Rank getRank(String rankId) {
        return this.collection.getCachedValued(rankId.toLowerCase());
    }

    public Collection<Rank> getRanks() {
        return this.collection.getCachedValues();
    }

    public Collection<Rank> getSortedRanks() {
        List<Rank> rankList = new ArrayList<>(this.getRanks());
        rankList.sort(Rank::compareTo);
        return rankList;
    }

    public CompletableFuture<Collection<Rank>> getSortedRanksAsync() {
        return CompletableFuture.supplyAsync(this::getSortedRanks);
    }

    public CompletableFuture<Rank> getRealValueAsync(String rankId) {
        return this.collection.getOrCreateRealValueAsync(rankId.toLowerCase());
    }

    public CompletableFuture<Void> updateRealValueAsync(String rankId, Consumer<Rank> consumer) {
        return this.collection.updateRealValueAsync(rankId.toLowerCase(), consumer);
    }

    public <T> CompletableFuture<T> updateRealValueWithFunctionAsync(String rankId, Function<Rank, T> function) {
        return this.collection.updateRealValueWithFunctionAsync(rankId.toLowerCase(), function);
    }
}
