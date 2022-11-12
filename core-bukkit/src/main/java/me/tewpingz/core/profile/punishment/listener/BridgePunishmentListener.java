package me.tewpingz.core.profile.punishment.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.alt.AltEntry;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.profile.punishment.event.PunishmentAddEvent;
import me.tewpingz.core.profile.punishment.event.PunishmentRemoveEvent;
import me.tewpingz.core.util.Broadcast;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BridgePunishmentListener {

    public BridgePunishmentListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(PunishmentAddEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());
            PunishmentType punishmentType = event.getPunishment().getPunishmentType();
            if (player != null) {
                instance.getPunishmentScheduleManager().schedule(event.getPlayerUuid(), event.getPunishment());
                switch (punishmentType) {
                    case BAN -> {
                        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                            player.kick(Component.text("You have been banned from the server").color(NamedTextColor.RED));
                        });
                    }
                    case BLACKLIST -> {
                        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                            player.kick(Component.text("You have been blacklisted from the server").color(NamedTextColor.RED));
                        });
                    }
                    case MUTE -> {
                        MessageBuilderDefaults.error().primary("You have been muted")
                                .build(player::sendMessage);
                    }
                }
            }

            if (punishmentType == PunishmentType.BLACKLIST) {
                Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealValue(event.getPlayerUuid());
                if (profile.getLastIp() != null) {
                    AltEntry.AltProfileSnapshot entry = Core.getInstance().getAltManager().getAlts(profile.getLastIp());
                    entry.getRelatedIds().forEach(uuid -> {
                        Player target = Bukkit.getPlayer(uuid);
                        if (target != null) {
                            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                                target.kick(Component.text("Your account is linked to account that has been blacklisted").color(NamedTextColor.RED));
                            });
                        }
                    });
                }
            }

            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutorName()).space()
                    .primary("has").space()
                    .primary(punishmentType == PunishmentType.BAN ? "banned" : (punishmentType == PunishmentType.MUTE ? "muted" : "blacklisted")).space()
                    .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                    .tertiary(".")
                    .toString(message -> Broadcast.broadcast(message, "core.grant.alert"));
        });

        instance.getCore().getBridge().registerListener(PunishmentRemoveEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());
            PunishmentType punishmentType = event.getExpiredPunishment().getPunishment().getPunishmentType();

            if (player != null && punishmentType == PunishmentType.MUTE) {
                instance.getPunishmentScheduleManager().unschedule(event.getPlayerUuid(), event.getExpiredPunishment());
                MessageBuilderDefaults.error().primary("Your mute").space()
                        .primary("has").space()
                        .secondary(event.getExpiredPunishment().getRemovedFor().equals("Expired") ? "expired" : "been removed")
                        .tertiary("!")
                        .build(player::sendMessage);
            }

            if (!event.getExpiredPunishment().getRemovedFor().equals("Expired")) {
                MessageBuilderDefaults.normal()
                        .tertiary("[Server Monitor]").space()
                        .secondary(event.getExecutorName()).space()
                        .primary("has").space()
                        .primary(punishmentType == PunishmentType.BAN ? "unbanned" : (punishmentType == PunishmentType.MUTE ? "unmuted" : "unblacklisted")).space()
                        .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                        .tertiary(".")
                        .toString(message -> Broadcast.broadcast(message, "core.grant.alert"));
            }
        });
    }
}
