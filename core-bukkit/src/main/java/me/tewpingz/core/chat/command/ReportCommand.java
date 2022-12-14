package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.PlayerReportEvent;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.entity.Player;

@CommandAlias("report")
@CommandPermission("core.report")
public class ReportCommand extends BaseCommand {
    @Default
    @CommandCompletion("@players @empty")
    @Syntax("<target> <reason>")
    public void onCommand(Player player, AsyncUuid target, String reason) {
        target.fetchUuid(player, uuid -> {
            if (uuid.equals(player.getUniqueId())) {
                Core.getInstance().getConfig().getErrorPalette().toBuilder()
                        .primary("You cannot report yourself")
                        .build(player::sendMessage);
                return;
            }

            Core.getInstance().getProfileManager().updateRealProfile(player.getUniqueId(), profile -> {
                if (!(profile.getReportCooldown() == -1 || (profile.getReportCooldown() - System.currentTimeMillis()) < 0)) {
                    Core.getInstance().getConfig().getErrorPalette().toBuilder()
                            .primary("You are currently on report cooldown for").space()
                            .secondary(TimeUtil.formatLongIntoDetailedString(profile.getReportCooldown() - System.currentTimeMillis()))
                            .tertiary(".")
                            .build(player::sendMessage);
                    return;
                }

                profile.setReportCooldown(System.currentTimeMillis() + 30_000);
                String server = CorePlugin.getInstance().getServerInitializer().getConfig().getServerName();
                Core.getInstance().getBridge().callEvent(new PlayerReportEvent(player.getName(), server, reason, uuid));
                Core.getInstance().getConfig().getSuccessPalette().toBuilder()
                        .primary("Your report has been successfully received")
                        .build(player::sendMessage);
            });
        });
    }
}
