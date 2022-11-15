package me.tewpingz.core.profile.grant.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.grant.event.GrantAddEvent;
import me.tewpingz.core.profile.grant.event.GrantRemoveEvent;
import me.tewpingz.core.util.Broadcast;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GrantBridgeListener {
    public GrantBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(GrantAddEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());

            if (player != null) {
                instance.getGrantScheduleManager().schedule(event.getPlayerUuid(), event.getGrant());
                MessageBuilderDefaults.success()
                        .primary("You have been given").space()
                        .append(event.getGrant().getRankNameComponent()).tertiary(".")
                        .build(player::sendMessage);

                Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealValue(player.getUniqueId());
                CorePlugin.getInstance().getGrantAttachmentManager().updateAttachment(player, profile);
            }

            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutorName()).space()
                    .primary("has given").space()
                    .append(event.getGrant().getRankNameComponent()).space()
                    .primary("to").space()
                    .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                    .tertiary(".")
                    .toString(message -> Broadcast.broadcast(message, "core.grant.alert"));
        });

        instance.getCore().getBridge().registerListener(GrantRemoveEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());

            if (player != null) {
                instance.getGrantScheduleManager().unschedule(event.getPlayerUuid(), event.getExpiredGrant());

                MessageBuilderDefaults.error().primary("Your").space()
                        .append(event.getExpiredGrant().getGrant().getRankNameComponent()).space()
                        .primary("grant has").space()
                        .primary(event.getExpiredGrant().getRemovedFor().equals("Expired") ? "expired" : "removed")
                        .tertiary("!")
                        .build(player::sendMessage);

                Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealValue(player.getUniqueId());
                CorePlugin.getInstance().getGrantAttachmentManager().updateAttachment(player, profile);
            }

            if (!event.getExpiredGrant().getRemovedFor().equals("Expired")) {
                MessageBuilderDefaults.normal()
                        .tertiary("[Server Monitor]").space()
                        .secondary(event.getExecutorName()).space()
                        .primary("has removed").space()
                        .append(event.getExpiredGrant().getGrant().getRankNameComponent()).space()
                        .primary("from").space()
                        .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                        .tertiary(".")
                        .toString(message -> Broadcast.broadcast(message, "core.grant.alert"));
            }
        });
    }
}
