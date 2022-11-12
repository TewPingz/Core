package me.tewpingz.core.server;

import lombok.Getter;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.server.event.ServerOnlineEvent;
import me.tewpingz.core.server.event.ServerShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ServerInitializer {

    private final String serverId, serverDisplayName;

    public ServerInitializer(CorePlugin instance) {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("server");
        this.serverId = section.getString("server-id").toLowerCase();
        this.serverDisplayName = section.getString("server-display-name");
        Core.getInstance().getServerManager().updateRealValueAsync(this.serverId, server -> {
            server.setDisplayName(this.serverDisplayName);
            server.setWhitelisted(Bukkit.hasWhitelist());
            server.setMaxPlayers(Bukkit.getMaxPlayers());
            server.setOnline(true);
        }).thenAccept(server -> Core.getInstance().getBridge().callEvent(new ServerOnlineEvent(server)));
    }

    public void shutdown() {
        Server.ServerSnapshot snapshot = Core.getInstance().getServerManager().updateRealValue(this.serverId, server -> {
            server.getPlayers().clear();
            server.setOnline(false);
        });
        Core.getInstance().getBridge().callEvent(new ServerShutdownEvent(snapshot));
    }
}
