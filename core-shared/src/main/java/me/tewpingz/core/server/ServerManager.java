package me.tewpingz.core.server;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.Collection;
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

    public Server.ServerSnapshot getCachedServer(String serverId) {
        return this.collection.getCachedValued(serverId.toLowerCase());
    }

    public Collection<Server.ServerSnapshot> getCachedServers() {
        return this.collection.getCachedValues();
    }

    public Server.ServerSnapshot updateRealRank(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValue(serverId.toLowerCase(), consumer);
    }

    public CompletableFuture<Server.ServerSnapshot> updateRealRankAsync(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValueAsync(serverId.toLowerCase(), consumer);
    }
}
