package me.tewpingz.core.queue;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class QueueManager {

    private final RediGoCollection<Queue.QueueSnapshot, String, Queue> collection;

    public QueueManager(Core instance) {
        this.collection = instance.getRediGo()
                .createCollection("queues", String.class, Queue.class, 30, true, Queue::new);
    }

    public Queue.QueueSnapshot updateQueue(String serverId, Consumer<Queue> consumer) {
        return this.collection.updateRealValue(serverId.toLowerCase(), consumer);
    }

    public CompletableFuture<Queue.QueueSnapshot> updateQueueAsync(String serverId, Consumer<Queue> consumer) {
        return this.collection.updateRealValueAsync(serverId.toLowerCase(), consumer);
    }

    public Collection<Queue.QueueSnapshot> getCachedQueues() {
        return this.collection.getCachedValues();
    }

    public Optional<Queue.QueueSnapshot> getQueueByPlayer(UUID uuid) {
        return this.collection.getCachedValues().stream()
                .filter(serverQueueSnapshot -> serverQueueSnapshot.hasPlayer(uuid)).findFirst();
    }
}
