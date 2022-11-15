package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.command.CommandSender;

@CommandAlias("unban")
@CommandPermission("core.unban")
public class UnbanCommand extends BaseCommand {
    @Default
    @Syntax("<target> <reason>")
    @CommandCompletion("@players @empty")
    public void onCommand(CommandSender commandSender, AsyncUuid asyncUuid, String reason) {
        asyncUuid.fetchUuid(commandSender, uuid -> {
            Core.getInstance().getProfileManager().updateRealValue(uuid, profile -> {
                boolean success = false;

                for (Punishment punishment : profile.getActivePunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).toList()) {
                    profile.removePunishment(punishment, commandSender.getName(), reason);
                    success = true;
                }

                if (success) {
                    MessageBuilderDefaults.success().secondary(profile.getLastSeenName()).space()
                            .primary("has been successfully unbanned").tertiary("!")
                            .build(commandSender::sendMessage);
                } else {
                    MessageBuilderDefaults.error().secondary(profile.getLastSeenName()).space()
                            .primary("was not even banned").tertiary("!")
                            .build(commandSender::sendMessage);
                }
            });
        });
    }
}
