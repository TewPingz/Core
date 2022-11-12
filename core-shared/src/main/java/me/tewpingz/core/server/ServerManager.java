package me.tewpingz.core.server;

import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.redigo.RediGoCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ServerManager {

    private final RediGoCollection<Server.ServerSnapshot, String, Server> collection;

    public ServerManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("servers", String.class, Server.class, 30, true, serverId -> {
            Server server = new Server(serverId);
            server.setDisplayName(serverId);
            return server;
        }, Server::new);
    }

    public Server.ServerSnapshot getServer(String serverId) {
        return this.collection.getCachedValued(serverId.toLowerCase());
    }

    public Collection<Server.ServerSnapshot> getServers() {
        return this.collection.getCachedValues();
    }

    public CompletableFuture<Server.ServerSnapshot> getRealValueAsync(String serverId) {
        return this.collection.getOrCreateRealValueAsync(serverId.toLowerCase());
    }

    public Server updateRealValue(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValue(serverId.toLowerCase(), consumer);
    }

    public CompletableFuture<Server> updateRealValueAsync(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValueAsync(serverId.toLowerCase(), consumer);
    }
}
