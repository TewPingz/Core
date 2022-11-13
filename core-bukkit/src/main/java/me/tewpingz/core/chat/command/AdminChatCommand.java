package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.AdminChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("adminchat|ac")
@CommandPermission("core.staffchat")
public class AdminChatCommand extends BaseCommand {
    @Default
    @Syntax("<message>")
    public void onCommand(CommandSender sender, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            String server = CorePlugin.getInstance().getServerInitializer().getServerDisplayName();
            Core.getInstance().getBridge().callEvent(new AdminChatEvent(sender.getName(), server, message));
        });
    }
}