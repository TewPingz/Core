package me.tewpingz.core.profile.punishment;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PunishmentListener implements Listener {

    private final PunishmentScheduleManager punishmentScheduleManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getInstance().getProfileManager().getRealValueAsync(event.getPlayer().getUniqueId()).thenAccept(profile -> {
            profile.getActivePunishments().forEach(punishment -> this.punishmentScheduleManager.schedule(event.getPlayer().getUniqueId(), punishment));;
        });
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Profile.ProfileSnapshot snapshot = Core.getInstance().getProfileManager().getCachedValue(event.getPlayer().getUniqueId());

        if (snapshot.getMute().isEmpty()) {
            return;
        }

        event.setCancelled(true);
        Punishment mute = snapshot.getMute().get();
        MessageBuilderDefaults.error()
                .primary("You cannot talk in chat as you are currently muted.").space()
                .append(Component.newline())
                .primary("Duration:").space()
                .secondary(mute.isInfinite() ? "Permanently" : TimeUtil.formatLongIntoDetailedString(mute.getTimeLeft()))
                .build(event.getPlayer()::sendMessage);
    }
}
