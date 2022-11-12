package me.tewpingz.core.profile.grant;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.grant.event.GrantCreateEvent;
import me.tewpingz.core.profile.grant.event.GrantRemoveEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GrantBridgeListener {
    public GrantBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(GrantCreateEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());

            if (player != null) {
                instance.getGrantScheduleManager().scheduleTask(event.getPlayerUuid(), event.getGrant());
                MessageBuilderDefaults.success()
                        .primary("You have been given").space()
                        .append(event.getGrant().getRankNameComponent()).space()
                        .tertiary(".")
                        .build(player::sendMessage);
            }

            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutorName()).space()
                    .primary("has given").space()
                    .append(event.getGrant().getRankNameComponent()).space()
                    .primary("to").space()
                    .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                    .tertiary(".")
                    .toString(this::broadcast);
        });

        instance.getCore().getBridge().registerListener(GrantRemoveEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());

            if (player != null) {
                instance.getGrantScheduleManager().unscheduleTask(event.getPlayerUuid(), event.getExpiredGrant());

                MessageBuilderDefaults.error().primary("Your").space()
                        .append(event.getExpiredGrant().getGrant().getRankNameComponent()).space()
                        .primary("grant has").space()
                        .primary(event.getExpiredGrant().getRemovedFor().equals("Expired") ? "expired" : "removed")
                        .tertiary("!")
                        .build(player::sendMessage);
            }

            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutorName()).space()
                    .primary("has removed").space()
                    .append(event.getExpiredGrant().getGrant().getRankNameComponent()).space()
                    .primary("from").space()
                    .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                    .tertiary(".")
                    .toString(this::broadcast);
        });
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.grant.alert") || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}
