package me.tewpingz.core.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.command.CommandSender;

@CommandAlias("ranks")
public class RanksCommand extends BaseCommand {

    @Default
    @CommandPermission("core.rank.list")
    public void onCommand(CommandSender commandSender) {
        CorePlugin.getInstance().getCore().getRankManager().getSortedRanksAsync().thenAccept(ranks -> {
            int amount = ranks.size();

            if (amount == 0) {
                commandSender.sendMessage(MessageBuilderDefaults.error().primary("There are no ranks").build());
                return;
            }

            commandSender.sendMessage(" ");
            commandSender.sendMessage(MessageBuilderDefaults.normal()
                    .primary("Here are the registered ranks")
                    .tertiary("(There %s %s)".formatted(amount == 1 ? "is" : "are", amount))
                    .build());

            ranks.forEach(rank -> commandSender.sendMessage(MessageBuilderDefaults.normal()
                    .secondary(" -")
                    .primary(rank.getDisplayName())
                    .tertiary("(Priority %s)".formatted(rank.getPriority()))
                    .build()));
            commandSender.sendMessage(" ");
        });
    }
}
