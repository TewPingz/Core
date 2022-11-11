package me.tewpingz.core.rank.grant;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> this.rankGrantScheduleManager.unscheduledTasks(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(final AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
            Profile.ProfileSnapshot snapshot = Core.getInstance().getProfileManager().getCachedValue(event.getPlayer().getUniqueId());
            TextComponent prefix = Component.text(snapshot.getDisplayRank().getPrefix());
            TextComponent suffix = Component.text(snapshot.getDisplayRank().getSuffix());
            TextComponent color = Component.text(snapshot.getDisplayRank().getColor());
            TextComponent separator = Component.text(": ").color(TextColor.color(220,220,220));
            return prefix.append(suffix).append(color).append(sourceDisplayName).append(suffix).append(separator).append(message);
        }));
    }
}
