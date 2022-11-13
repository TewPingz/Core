package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.command.CommandSender;

@CommandAlias("unban")
@CommandPermission("core.unban")
public class UnbanCommand extends BaseCommand {
    @Default
    @Syntax("<target> <reason>")
    @CommandCompletion("@players Unbanned")
    public void onCommand(CommandSender commandSender, AsyncUuid asyncUuid, String reason) {
        asyncUuid.fetchUuid(commandSender, uuid -> {
            Core.getInstance().getProfileManager().updateRealValue(uuid, profile -> {
                profile.getActivePunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).toList().forEach(punishment -> {
                    profile.removePunishment(punishment, commandSender.getName(), reason);
                });
            });
        });
    }
}
