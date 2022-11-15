package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;

@CommandAlias("list|players")
@CommandPermission("core.list")
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

            List<Profile.ProfileSnapshot> profiles = Core.getInstance().getProfileManager().getCachedValues().stream()
                    .sorted(Comparator.comparingInt(o -> -o.getDisplayRank().getPriority())).toList();

            profiles.forEach(profile -> {
                if (!builder.isEmpty()) {
                    builder.tertiary(",").space();
                }
                builder.append(profile.getDisplayRank().getColor().apply(Component.text(profile.getLastSeenName())));
            });

            Component component = Component.text("(" + profiles.size() + "/" + Bukkit.getMaxPlayers() + "): ")
                    .color(NamedTextColor.GRAY)
                    .append(builder.build());

            sender.sendMessage("");
            sender.sendMessage(rankList);
            sender.sendMessage(component);
            sender.sendMessage("");
        });
    }

}
