package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("unblacklist")
@CommandPermission("core.unblacklist")
public class UnblacklistCommand extends BaseCommand {
    @Default
    @Syntax("<target> <reason>")
    @CommandCompletion("@players @empty")
    public void onCommand(CommandSender commandSender, AsyncUuid asyncUuid, String reason) {
        asyncUuid.fetchUuid(commandSender, uuid -> {
            Core.getInstance().getProfileManager().updateRealProfile(uuid, profile -> {
                Set<Punishment> punishments = profile.getActivePunishments().stream()
                        .filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST)
                        .collect(Collectors.toSet());;

                if (!punishments.isEmpty()) {
                    for (Punishment punishment : punishments) {
                        profile.removePunishment(punishment, commandSender.getName(), reason);
                    }

                    Core.getInstance().getConfig().getSuccessPalette().toBuilder()
                            .secondary(profile.getLastSeenName()).space()
                            .primary("has been successfully unblacklisted").tertiary("!")
                            .build(commandSender::sendMessage);
                } else {
                    Core.getInstance().getConfig().getErrorPalette().toBuilder()
                            .secondary(profile.getLastSeenName()).space()
                            .primary("was not even blacklisted").tertiary("!")
                            .build(commandSender::sendMessage);
                }
            });
        });
    }
}
