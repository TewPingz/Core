package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("setmax|setmaxplayers")
@CommandPermission("core.setmax")
public class SetMaxCommand extends BaseCommand {
    @Default
    @Syntax("<max>")
    public void onCommand(CommandSender sender, int max) {
        Bukkit.setMaxPlayers(max);

        String serverId = CorePlugin.getInstance().getServerInitializer().getConfig().getServerId();
        Core.getInstance().getServerManager().updateRealValueAsync(serverId, server -> {
            server.setMaxPlayers(max);
        }).thenRun(() -> {
            MessageBuilderDefaults.success().primary("You have successfully set the max player count of this server to").space()
                    .secondary(max).tertiary("!")
                    .build(sender::sendMessage);
        });
    }
}
