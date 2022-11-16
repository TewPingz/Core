package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.PlayerRequestEvent;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.entity.Player;

@CommandAlias("request")
@CommandPermission("core.request")
public class RequestCommand extends BaseCommand {
    @Default
    @Syntax("<message>")
    @CommandCompletion("@empty")
    public void onCommand(Player player, String message) {
        Core.getInstance().getProfileManager().updateRealProfileAsync(player.getUniqueId(), profile -> {
            if (!(profile.getRequestCooldown() == -1 || (profile.getRequestCooldown() - System.currentTimeMillis()) < 0)) {
                MessageBuilderDefaults.error()
                        .primary("You are currently on request cooldown for").space()
                        .secondary(TimeUtil.formatLongIntoDetailedString(profile.getRequestCooldown() - System.currentTimeMillis()))
                        .tertiary(".")
                        .build(player::sendMessage);
                return;
            }

            profile.setRequestCooldown(System.currentTimeMillis() + 30_000);
            String server = CorePlugin.getInstance().getServerInitializer().getConfig().getServerName();
            Core.getInstance().getBridge().callEvent(new PlayerRequestEvent(player.getName(), server, message));
            MessageBuilderDefaults.success()
                    .primary("Your report has been successfully received")
                    .build(player::sendMessage);
        });
    }
}
