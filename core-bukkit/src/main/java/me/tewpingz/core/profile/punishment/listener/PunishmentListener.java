package me.tewpingz.core.profile.punishment.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.profile.punishment.PunishmentScheduleManager;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PunishmentListener implements Listener {

    private final PunishmentScheduleManager punishmentScheduleManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getInstance().getProfileManager().getRealValueAsync(event.getPlayer().getUniqueId()).thenAccept(profile -> {
            profile.getSortedActivePunishments().forEach(punishment -> this.punishmentScheduleManager.schedule(event.getPlayer().getUniqueId(), punishment));;
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> this.punishmentScheduleManager.terminate(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Profile.ProfileSnapshot snapshot = Core.getInstance().getProfileManager().getCachedValue(event.getPlayer().getUniqueId());
        Punishment mute = snapshot.getMute();

        if (mute == null) {
            return;
        }

        event.setCancelled(true);
        MessageBuilderDefaults.error()
                .primary("You cannot talk in chat as you are currently muted.").space()
                .append(Component.newline())
                .primary("Duration:").space()
                .secondary(mute.isInfinite() ? "Permanently" : TimeUtil.formatLongIntoDetailedString(mute.getTimeLeft()))
                .build(event.getPlayer()::sendMessage);
    }
}