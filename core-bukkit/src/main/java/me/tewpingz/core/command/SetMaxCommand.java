package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("setmax|setmaxplayers")
@CommandPermission("core.setmax")
public class SetMaxCommand extends BaseCommand {
    @Default
    @Syntax("<max>")
    @CommandCompletion("@empty")
    public void onCommand(CommandSender sender, int max) {
        Bukkit.setMaxPlayers(max);

        String serverId = CorePlugin.getInstance().getServerInitializer().getConfig().getServerId();
        Core.getInstance().getServerManager().updateRealRankAsync(serverId, server -> {
            server.setMaxPlayers(max);
        }).thenRun(() -> {
            Core.getInstance().getConfig().getSuccessPalette().toBuilder()
                    .primary("You have successfully set the max player count of this server to").space()
                    .secondary(max).tertiary("!")
                    .build(sender::sendMessage);
        });
    }
}
