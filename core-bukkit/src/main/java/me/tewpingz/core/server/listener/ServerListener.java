package me.tewpingz.core.server.listener;

import com.destroystokyo.paper.event.server.WhitelistToggleEvent;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.server.ServerInitializer;
import me.tewpingz.core.server.event.ServerWhitelistEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ServerListener implements Listener {

    private final ServerInitializer serverInitializer;

    @EventHandler
    public void onWhitelistToggle(WhitelistToggleEvent event) {
        Core.getInstance().getServerManager().updateRealValueAsync(this.serverInitializer.getServerId(), server -> {
            server.setWhitelisted(event.isEnabled());
        }).thenAccept(server -> Core.getInstance().getBridge().callEvent(new ServerWhitelistEvent(server)));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getInstance().getServerManager().updateRealValueAsync(this.serverInitializer.getServerId(), server -> {
            server.addPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Core.getInstance().getServerManager().updateRealValueAsync(this.serverInitializer.getServerId(), server -> {
            server.removePlayer(event.getPlayer().getUniqueId());
        });
    }
}
