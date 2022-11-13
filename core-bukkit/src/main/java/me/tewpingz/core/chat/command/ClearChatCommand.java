package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.util.Broadcast;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("clearchat|cc")
@CommandPermission("core.clearchat")
public class ClearChatCommand extends BaseCommand {
    @Default
    public void onCommand(CommandSender sender) {
        List<? extends Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.hasPermission("core.clearchat.bypass") && !player.isOp())
                .toList();

        for (int i = 0; i < 500; i++) {
            players.forEach(player -> player.sendMessage(Component.empty()));
        }

        MessageBuilderDefaults.normal()
                .secondary(sender.getName()).space()
                .primary("has cleared the chat").tertiary(".")
                .build(Bukkit::broadcast);
    }
}
