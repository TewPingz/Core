package me.tewpingz.core.profile;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final ProfileManager profileManager;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        this.profileManager.updateRealValueAsync(uuid, profile -> {
            profile.setLastSeenName(event.getName());
            profile.setLastSeen(-1);
        });

        this.profileManager.beginCachingLocally(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        this.profileManager.stopCachingLocally(uuid);
        this.profileManager.updateRealValueAsync(uuid, profile -> profile.setLastSeen(System.currentTimeMillis()));
    }
}
