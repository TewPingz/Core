package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.StaffChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("staffchat|sc")
@CommandPermission("core.staffchat")
public class StaffChatCommand extends BaseCommand {
    @Default
    @Syntax("<message>")
    @CommandCompletion("@empty")
    public void onCommand(CommandSender sender, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            String server = CorePlugin.getInstance().getServerInitializer().getConfig().getServerName();
            Core.getInstance().getBridge().callEvent(new StaffChatEvent(sender.getName(), server, message));
        });
    }
}
