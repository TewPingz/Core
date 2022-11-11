package me.tewpingz.core.rank.grant;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class RankGrantListener implements Listener {

    private final RankGrantScheduleManager rankGrantScheduleManager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getInstance().getProfileManager().updateRealValueAsync(event.getPlayer().getUniqueId(), profile -> {
            profile.getActiveGrants().stream().filter(grant -> grant.getRankSnapshot() == null).toList().forEach(grant -> {
                profile.removeGrant(grant, "CONSOLE", "Rank no-longer exists");
            });

            profile.getActiveGrants().stream().filter(RankGrant::hasExpired).toList().forEach(grant -> {
                profile.removeGrant(grant, "CONSOLE", "Expired while offline");
            });

            profile.getActiveGrants().forEach(grant -> this.rankGrantScheduleManager.scheduleTask(event.getPlayer().getUniqueId(), grant));
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Profile.ProfileSnapshot snapshot = Core.getInstance().getProfileManager().getCachedValue(event.getPlayer().getUniqueId());
        String prefix = snapshot.getDisplayRank().getPrefix();
        String suffix = snapshot.getDisplayRank().getSuffix();
        String color = snapshot.getDisplayRank().getColor();
        event.getPlayer().setDisplayName(color + event.getPlayer().getName());
        event.setFormat(prefix + "%s" + suffix + ChatColor.WHITE + ": %s");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> this.rankGrantScheduleManager.unscheduledTasks(event.getPlayer().getUniqueId()));
    }
}
