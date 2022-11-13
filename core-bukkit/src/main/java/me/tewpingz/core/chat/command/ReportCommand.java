package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.PlayerReportEvent;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.entity.Player;

@CommandAlias("report")
@CommandPermission("core.report")
public class ReportCommand extends BaseCommand {
    @Default
    @Syntax("<target> <reason>")
    public void onCommand(Player player, AsyncUuid target, String reason) {
        target.fetchUuid(player, uuid -> {
            if (uuid.equals(player.getUniqueId())) {
                MessageBuilderDefaults.error()
                        .primary("You cannot report yourself")
                        .build(player::sendMessage);
                return;
            }

            Core.getInstance().getProfileManager().updateRealValue(player.getUniqueId(), profile -> {
                if (!(profile.getLastReportExecuted() == -1 || (System.currentTimeMillis() - profile.getLastReportExecuted()) > 30_000)) {
                    MessageBuilderDefaults.error()
                            .primary("You are currently on report cooldown")
                            .tertiary(".")
                            .build(player::sendMessage);
                    return;
                }

                profile.setLastReportExecuted(System.currentTimeMillis());
                String server = CorePlugin.getInstance().getServerInitializer().getServerDisplayName();
                Core.getInstance().getBridge().callEvent(new PlayerReportEvent(player.getName(), server, reason, uuid));
            });
        });
    }
}
