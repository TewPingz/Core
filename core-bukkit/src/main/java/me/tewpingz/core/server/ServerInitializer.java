package me.tewpingz.core.server;

import lombok.Getter;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.server.event.ServerOnlineEvent;
import me.tewpingz.core.server.event.ServerShutdownEvent;
import org.bukkit.Bukkit;

@Getter
public class ServerInitializer {

    private final ServerConfig config;

    public ServerInitializer(CorePlugin instance) {
        this.config = ServerConfig.getServerConfig(instance.getDataFolder());
        Core.getInstance().getServerManager().updateRealValueAsync(this.config.getServerId(), server -> {
            server.setDisplayName(this.config.getServerName());
            server.setWhitelisted(Bukkit.hasWhitelist());
            server.setMaxPlayers(Bukkit.getMaxPlayers());
            server.setOnline(true);
        }).thenAccept(server -> Core.getInstance().getBridge().callEvent(new ServerOnlineEvent(server)));
    }

    public void shutdown() {
        Server.ServerSnapshot snapshot = Core.getInstance().getServerManager().updateRealValue(this.config.getServerId(), server -> {
            server.getPlayers().clear();
            server.setOnline(false);
        });
        Core.getInstance().getBridge().callEvent(new ServerShutdownEvent(snapshot));
    }
}
