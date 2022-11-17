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
        Core.getInstance().getProfileManager().getRealProfileAsync(event.getPlayer().getUniqueId()).thenAccept(profile -> {
            profile.getActivePunishments().forEach(punishment -> this.punishmentScheduleManager.schedule(event.getPlayer().getUniqueId(), punishment));;
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> this.punishmentScheduleManager.terminate(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getCachedProfile(event.getPlayer().getUniqueId());
        Punishment mute = profile.getMute();

        if (mute == null) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage(mute.formatKickMessage(false));
    }
}
