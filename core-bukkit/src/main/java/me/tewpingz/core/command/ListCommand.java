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
        Core.getInstance().getRankManager().getCachedSortedRanksAsync().thenApply(ranks -> {
            MessageBuilder builder = Core.getInstance().getConfig().getDefaultPallet().toBuilder(false);
            ranks.forEach(rank -> {
                if (!builder.isEmpty()) {
                    builder.tertiary(",").space();
                }
                builder.append(rank.getDisplayNameWithColor());
            });
            return builder.build();
        }).thenAccept(rankList -> {
            MessageBuilder builder = Core.getInstance().getConfig().getDefaultPallet().toBuilder(false);

            List<Profile.ProfileSnapshot> profiles = Core.getInstance().getProfileManager().getCachedProfiles().stream()
                    .sorted(Comparator.comparingInt(o -> -o.getDisplayRank().getPriority())).toList();

            profiles.forEach(profile -> {
                if (!builder.isEmpty()) {
                    builder.tertiary(",").space();
                }
                builder.append(profile.getDisplayRank().getColor().apply(Component.text(profile.getLastSeenName())));
            });

            sender.sendMessage("");
            sender.sendMessage(rankList);
            Core.getInstance().getConfig().getDefaultPallet().toBuilder(false)
                    .tertiary("(%s/%s):".formatted(profiles.size(), Bukkit.getMaxPlayers())).space()
                    .append(builder.build())
                    .build(sender::sendMessage);
            sender.sendMessage("");
        });
    }

}
