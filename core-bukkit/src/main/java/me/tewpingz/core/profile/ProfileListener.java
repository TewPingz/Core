package me.tewpingz.core.profile;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final ProfileManager profileManager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        this.profileManager.updateRealValueAsync(uuid, profile -> {
            profile.setLastSeenName(event.getName());
            profile.setLastSeen(-1);
        });

        this.profileManager.beginCachingLocally(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // Make sure to unload the profile from cache if the login is cancelled.
            this.profileManager.stopCachingLocally(event.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        this.profileManager.stopCachingLocally(uuid);
        this.profileManager.updateRealValueAsync(uuid, profile -> profile.setLastSeen(System.currentTimeMillis()));
    }
}
