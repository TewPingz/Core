package me.tewpingz.core.profile.grant.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.grant.Grant;
import me.tewpingz.core.profile.grant.GrantScheduleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GrantListener implements Listener {

    private final GrantScheduleManager grantScheduleManager;
    private final ChatRenderer formatRenderer;

    public GrantListener(GrantScheduleManager grantScheduleManager) {
        this.grantScheduleManager = grantScheduleManager;
        this.formatRenderer = ChatRenderer.viewerUnaware((source, displayName, message) -> {
            Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getCachedValue(source.getUniqueId());
            TextComponent prefix = Component.text(profile.getDisplayRank().getPrefix());
            TextComponent suffix = Component.text(profile.getDisplayRank().getSuffix());
            TextComponent separator = Component.text(": ").color(NamedTextColor.GRAY);
            return prefix
                    .append(suffix)
                    .append(profile.getDisplayRank().getColor().apply(displayName))
                    .append(suffix)
                    .append(separator)
                    .append(message);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getInstance().getProfileManager().updateRealValueAsync(event.getPlayer().getUniqueId(), profile -> {
            profile.getActiveGrants().stream().filter(grant -> grant.getRankSnapshot() == null).toList().forEach(grant -> {
                profile.removeGrant(grant, "CONSOLE", "Rank no-longer exists");
            });

            profile.getActiveGrants().stream().filter(Grant::hasExpired).toList().forEach(grant -> {
                profile.removeGrant(grant, "CONSOLE", "Expired");
            });

            profile.getActiveGrants().forEach(grant -> this.grantScheduleManager.schedule(event.getPlayer().getUniqueId(), grant));
        }).thenAccept(profile -> {
            CorePlugin.getInstance().getGrantAttachmentManager().createAttachment(event.getPlayer(), profile);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> this.grantScheduleManager.terminate(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(final AsyncChatEvent event) {
        event.renderer(this.formatRenderer);
    }
}
