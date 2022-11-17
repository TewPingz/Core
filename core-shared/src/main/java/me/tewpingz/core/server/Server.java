package me.tewpingz.core.server;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Server implements RediGoObject<String, Server.ServerSnapshot> {

    private final String serverId;

    @RediGoValue(key = "displayName")
    private String displayName;

    @RediGoValue(key = "players")
    private Map<UUID, ServerPlayer> players = new HashMap<>();

    @RediGoValue(key = "whitelisted")
    private boolean whitelisted = false;

    @RediGoValue(key = "online")
    private boolean online = false;

    @RediGoValue(key = "maxPlayers")
    private int maxPlayers = 0;

    /**
     * A function to add a player to the server
     * @param playerId the player id
     * @param playerName the name of the player
     */
    public void addPlayer(UUID playerId, String playerName) {
        this.players.put(playerId, new ServerPlayer(playerId, playerName));
    }

    /**
     * A function to remove a player from the server
     * @param playerId the player id
     */
    public void removePlayer(UUID playerId) {
        this.players.remove(playerId);
    }

    @Override
    public String getKey() {
        return this.serverId;
    }

    @Override
    public Server.ServerSnapshot getSnapshot() {
        return new ServerSnapshot(this.serverId, this.displayName, this.players, this.whitelisted, this.online, this.maxPlayers);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ServerSnapshot implements Snapshot {
        private final String serverId, displayName;
        private final Map<UUID, ServerPlayer> players;
        private final boolean whitelisted, online;
        private final int maxPlayers;

        /**
         * A function to get a player from the server
         * @param uuid the uuid of the player
         * @return a {@link ServerPlayer} instance
         */
        public ServerPlayer getPlayer(UUID uuid) {
            return this.players.get(uuid);
        }

        /**
         * A function to get all the players from the server
         * @return a collection of {@link ServerPlayer} online on that server
         */
        public Collection<ServerPlayer> getPlayers() {
            return this.players.values();
        }
    }
}
