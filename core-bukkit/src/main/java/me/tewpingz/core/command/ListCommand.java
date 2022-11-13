package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("list|players")
public class ListCommand extends BaseCommand {
    @Default
    public void onCommand(CommandSender sender) {
        Core.getInstance().getRankManager().getSortedRanksAsync().thenApply(ranks -> {
            MessageBuilder builder = MessageBuilderDefaults.normal();
            ranks.forEach(rank -> {
                if (!builder.isEmpty()) {
                    builder.tertiary(",").space();
                }
                builder.append(rank.getColor().apply(Component.text(rank.getDisplayName())));
            });
            return builder.build();
        }).thenAccept(rankList -> {
            MessageBuilder builder = MessageBuilderDefaults.normal();
            Core.getInstance().getProfileManager().forEachCachedValue(profile -> {
                if (!builder.isEmpty()) {
                    builder.tertiary(",").space();
                }
                builder.append(profile.getDisplayRank().getColor().apply(Component.text(profile.getLastSeenName())));
            });

            int amount = Core.getInstance().getProfileManager().getCachedValues().size();
            Component component = Component.text("(" + amount + "/" + Bukkit.getMaxPlayers() + "): ")
                    .color(NamedTextColor.GRAY)
                    .append(builder.build());

            sender.sendMessage("");
            sender.sendMessage(rankList);
            sender.sendMessage(component);
            sender.sendMessage("");
        });
    }

}
