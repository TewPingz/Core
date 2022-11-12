package me.tewpingz.core.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Server implements RediGoObject<String, Server.ServerSnapshot> {

    private final String serverId;

    @RediGoValue(key = "displayName")
    private String displayName;

    @RediGoValue(key = "players")
    private Set<ServerPlayer> players = new HashSet<>();

    @RediGoValue(key = "whitelisted")
    private boolean whitelisted = false;

    @RediGoValue(key = "online")
    private boolean online = false;

    @RediGoValue(key = "maxPlayers")
    private int maxPlayers = 0;

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
        private final Set<ServerPlayer> players;
        private final boolean whitelisted, online;
        private final int maxPlayers;

    }
}
