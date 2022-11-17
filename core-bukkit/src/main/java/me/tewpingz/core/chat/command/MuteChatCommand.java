package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandPermission("core.mutechat")
@CommandAlias("mutechat|mc")
public class MuteChatCommand extends BaseCommand {
    @Default
    public void onCommand(CommandSender sender) {
        CorePlugin.getInstance().getChatManager().setChatEnabled(!CorePlugin.getInstance().getChatManager().isChatEnabled());
        Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                .primary("The public chat has been").space()
                .secondary(CorePlugin.getInstance().getChatManager().isChatEnabled() ? "enabled" : "disabled").space()
                .primary("by").space()
                .secondary(sender.getName())
                .tertiary(".")
                .build(Bukkit::broadcast);
    }
}
