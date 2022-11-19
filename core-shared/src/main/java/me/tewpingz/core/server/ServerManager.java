package me.tewpingz.core.server;

import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ServerManager {

    private final RediGoCollection<Server.ServerSnapshot, String, Server> collection;

    /**
     * The constructor of the server manager to initialize the RediGo collection
     * @param instance the instance of the Core
     */
    public ServerManager(Core instance) {
        this.collection = instance.getRediGo().createCollection("servers", String.class, Server.class, 30, true, serverId -> {
            Server server = new Server(serverId);
            server.setDisplayName(serverId);
            return server;
        }, Server::new);
    }

    /**
     * A function to get a cached server
     * @param serverId the server id
     * @return a ServerSnapshot of the server
     */
    public Server.ServerSnapshot getCachedServer(String serverId) {
        return this.collection.getCachedValued(serverId.toLowerCase());
    }

    /**
     * A function to get all the cached servers
     * @return a collection of {@link me.tewpingz.core.server.Server.ServerSnapshot}
     */
    public Collection<Server.ServerSnapshot> getCachedServers() {
        return this.collection.getCachedValues();
    }

    /**
     * A function to update the real rank
     * @param serverId the server id
     * @param consumer the consumer to apply edits to the server
     * @return the Snapshot of the latest server
     */
    public Server.ServerSnapshot updateRealRank(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValue(serverId.toLowerCase(), consumer);
    }

    /**
     * A function to update the real rank
     * This is called asynchronously
     * @param serverId the server id
     * @param consumer the consumer to apply edits to the server
     * @return the Snapshot of the latest server
     */
    public CompletableFuture<Server.ServerSnapshot> updateRealRankAsync(String serverId, Consumer<Server> consumer) {
        return this.collection.updateRealValueAsync(serverId.toLowerCase(), consumer);
    }
}
