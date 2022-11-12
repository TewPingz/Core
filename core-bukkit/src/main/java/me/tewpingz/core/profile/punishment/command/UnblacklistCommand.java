package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.command.CommandSender;

@CommandAlias("unblacklist")
@CommandPermission("core.unblacklist")
public class UnblacklistCommand extends BaseCommand {
    @Default
    @CommandCompletion("@players Unbanned")
    public void onCommand(CommandSender commandSender, AsyncUuid asyncUuid, String reason) {
        asyncUuid.fetchUuid(commandSender, uuid -> {
            Core.getInstance().getProfileManager().updateRealValue(uuid, profile -> {
                profile.getActivePunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).toList().forEach(punishment -> {
                    profile.removePunishment(punishment, commandSender.getName(), reason);
                });
            });
        });
    }
}