package me.tewpingz.core.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@CommandAlias("ranks")
@CommandPermission("core.ranks")
public class RanksCommand extends BaseCommand {
    @Default
    public void onCommand(CommandSender commandSender) {
        CorePlugin.getInstance().getCore().getRankManager().getCachedSortedRanksAsync().thenAccept(ranks -> {
            int amount = ranks.size();

            if (amount == 0) {
                commandSender.sendMessage(MessageBuilderDefaults.error().primary("There are no ranks").build());
                return;
            }

            commandSender.sendMessage(" ");
            MessageBuilderDefaults.normal()
                    .primary("Here are the registered ranks").space()
                    .tertiary("(There %s %s)".formatted(amount == 1 ? "is" : "are", amount))
                    .build(commandSender::sendMessage);

            ranks.forEach(rank -> {
                MessageBuilderDefaults.normal().space()
                        .secondary("-").space()
                        .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                        .tertiary("(Priority %s)".formatted(rank.getPriority()))
                        .build(commandSender::sendMessage);
            });

            commandSender.sendMessage(" ");
        });
    }
}
