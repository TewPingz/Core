package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@CommandAlias("punish")
@CommandPermission("core.punish")
public class PunishCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();;
    }

    @Subcommand("ban")
    @CommandPermission("core.punish.ban")
    @CommandCompletion("@players @duration Banned")
    @Syntax("<player> <duration> [reason...]")
    public void onBan(CommandSender sender, AsyncUuid asyncUuid, Duration duration, String reason) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Profile profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            profile.addPunishment(PunishmentType.BAN, sender.getName(), reason, duration.toMillis());
        });
    }

    @Subcommand("mute")
    @CommandPermission("core.punish.mute")
    @CommandCompletion("@players @duration Muted")
    @Syntax("<player> <duration> [reason...]")
    public void onMute(CommandSender sender, AsyncUuid asyncUuid, Duration duration, String reason) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Profile profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            profile.addPunishment(PunishmentType.MUTE, sender.getName(), reason, duration.toMillis());
        });
    }

    @Subcommand("blacklist")
    @CommandPermission("core.punish.blacklist")
    @Syntax("<player> <duration> [reason...]")
    @CommandCompletion("@players @duration Blacklisted")
    public void onBlacklist(CommandSender sender, AsyncUuid asyncUuid, Duration duration, String reason) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Profile profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            profile.addPunishment(PunishmentType.BLACKLIST, sender.getName(), reason, duration.toMillis());
        });
    }
}
