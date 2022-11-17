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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishmentBridgeListener {

    public PunishmentBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(PunishmentAddEvent.class, (charSequence, event) -> {
            Player player = Bukkit.getPlayer(event.getPlayerUuid());
            PunishmentType punishmentType = event.getPunishment().getPunishmentType();
            if (player != null) {
                instance.getPunishmentScheduleManager().schedule(event.getPlayerUuid(), event.getPunishment());
                switch (punishmentType) {
                    case BAN, BLACKLIST -> {
                        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                            player.kick(event.getPunishment().formatKickMessage(true));
                        });
                    }
                    case MUTE -> {
                        player.sendMessage(event.getPunishment().formatKickMessage(true));
                    }
                }
            }

            if (punishmentType == PunishmentType.BLACKLIST || punishmentType == PunishmentType.IP_BAN) {
                Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealProfile(event.getPlayerUuid());
                if (profile.getLastIp() != null) {
                    AltEntry.AltProfileSnapshot entry = Core.getInstance().getAltManager().getAlts(profile.getLastIp());
                    entry.getRelatedIds().forEach(uuid -> {
                        Player target = Bukkit.getPlayer(uuid);
                        if (target != null) {
                            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> target.kick(event.getPunishment().formatKickRelatedMessage(true)));
                        }
                    });
                }
            }

            Core.getInstance().getConfig().getDefaultPallet().toBuilder(false)
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
                Core.getInstance().getConfig().getSuccessPallet().toBuilder()
                        .primary("Your mute").space()
                        .primary("has").space()
                        .secondary(event.getExpiredPunishment().getRemovedFor().equals("Expired") ? "expired" : "been removed")
                        .tertiary("!")
                        .build(player::sendMessage);
            }

            if (!event.getExpiredPunishment().getRemovedFor().equals("Expired")) {
                Core.getInstance().getConfig().getDefaultPallet().toBuilder(false)
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
