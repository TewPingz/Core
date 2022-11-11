package me.tewpingz.core.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("rank")
@CommandPermission("core.rank")
public class RankCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Syntax("[page]")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Create a rank")
    @CommandPermission("core.rank.create")
    @Syntax("<name>")
    public static void onCreate(CommandSender commandSender, String rankName) {
        RankManager rankManager = Core.getInstance().getRankManager();

        if (rankManager.getRank(rankName) != null) {
            MessageBuilderDefaults.error()
                    .primary("There is already a rank that is named").space()
                    .secondary(rankName).space()
                    .tertiary("(You can check the existing ranks using /ranks)")
                    .build(commandSender::sendMessage);
            return;
        }

        rankManager.updateRealValueAsync(rankName.toLowerCase(), rank -> {
            rank.setDisplayName(rankName);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully created a rank named").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName())))
                    .tertiary(".")
                    .build(commandSender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankCreateEvent(commandSender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("setpriority|priority")
    @Description("Set a ranks priority")
    @CommandPermission("core.rank.setpriority")
    @Syntax("<rank> <priority>")
    @CommandCompletion("@ranks")
    public void onSetPriority(CommandSender sender, Rank.RankSnapshot rankSnapshot, int priority) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setPriority(priority);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the priority of")
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                    .primary("to").space()
                    .secondary(String.valueOf(priority)).tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("setprefix|prefix")
    @Description("Set a ranks prefix")
    @CommandPermission("core.rank.setprefix")
    @Syntax("<rank> <prefix>")
    @CommandCompletion("@ranks")
    public void onSetPrefix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String prefix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        String translatedPrefix = ChatColor.translateAlternateColorCodes('&', prefix);
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setPrefix(translatedPrefix);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the prefix of").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                    .primary("to").space()
                    .secondary(translatedPrefix).tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("setsuffix|suffix")
    @Description("Set a ranks suffix")
    @CommandPermission("core.rank.setsuffix")
    @Syntax("<rank> <suffix>")
    @CommandCompletion("@ranks")
    public void onSetSuffix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String suffix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        String translatedSuffix = ChatColor.translateAlternateColorCodes('&', suffix);
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setSuffix(translatedSuffix);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the suffix of").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                    .secondary(translatedSuffix)
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("setcolor|setcolour|color|colour")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <red> <green> <blue>")
    @CommandCompletion("@ranks")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, int red, int green, int blue) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().updateColor(red, green, blue);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the color of").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("setcolorhex|setcolourhex|colorhex|colourhex")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <red> <green> <blue>")
    @CommandCompletion("@ranks")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, String hex) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().updateColor(hex);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the color of").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName()))).space()
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }


    @Subcommand("permission")
    @Description("Add/Remove a ranks permission")
    @CommandPermission("core.rank.permission")
    @Syntax("<rank> <permission>")
    @CommandCompletion("@ranks")
    public void onPermission(CommandSender sender, Rank.RankSnapshot rankSnapshot, @Single String permission) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            if (realRank.getPermissions().contains(permission)) {
                realRank.getPermissions().remove(permission);
            } else {
                realRank.getPermissions().add(permission);
            }
        }).thenAccept(rank -> {
            boolean added = rank.getPermissions().contains(permission);
            sender.sendMessage(MessageBuilderDefaults.success()
                    .primary("You have successfully").space()
                    .secondary(added ? "added" : "removed").space()
                    .secondary(permission).space()
                    .primary("to").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName())))
                    .tertiary(".")
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("inherit")
    @Description("Add/Remove a ranks inherit")
    @CommandPermission("core.rank.inherit")
    @Syntax("<rank> <inheritRank>")
    @CommandCompletion("@ranks")
    public void onInherit(CommandSender sender, Rank.RankSnapshot rankSnapshot, Rank.RankSnapshot inherit) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> {
            if (realRank.getInherits().contains(inherit.getRankId())) {
                realRank.getInherits().remove(inherit.getRankId());
            } else {
                realRank.getInherits().add(inherit.getRankId());
            }
        }).thenAccept(rank -> {
            boolean added = rank.getInherits().contains(inherit.getRankId());
            sender.sendMessage(MessageBuilderDefaults.success()
                    .primary("You have successfully").space()
                    .secondary(added ? "added" : "removed").space()
                    .append(inherit.getColor().apply(Component.text(inherit.getDisplayName())))
                    .primary("as an inherit to").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName())))
                    .tertiary(".")
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank.getSnapshot()));
        });
    }

    @Subcommand("info")
    @Description("Check a ranks information")
    @CommandPermission("core.rank.info")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onInfo(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealValueAsync(rankSnapshot.getRankId()).thenAccept(rank -> {
            sender.sendMessage(" ");

            MessageBuilderDefaults.normal()
                    .primary("This is the information for").space()
                    .append(rank.getColor().apply(Component.text(rank.getDisplayName())))
                    .build(sender::sendMessage);


            MessageBuilderDefaults.normal().space()
                    .tertiary("-").space()
                    .primary("Priority:").space()
                    .secondary(rank.getPriority())
                    .build(sender::sendMessage);

            MessageBuilderDefaults.normal().space()
                    .tertiary("-").space()
                    .primary("Prefix:").space()
                    .secondary(rank.getPrefix().isEmpty() ? "N/A" : rank.getPrefix())
                    .build(sender::sendMessage);

            MessageBuilderDefaults.normal().space()
                    .tertiary("-").space()
                    .primary("Suffix:").space()
                    .secondary(rank.getSuffix().isEmpty() ? "N/A" : rank.getSuffix())
                    .build(sender::sendMessage);

            if (rank.getPermissions().isEmpty()) {
                MessageBuilderDefaults.normal().space()
                        .tertiary("-").space()
                        .primary("Permissions:").space()
                        .secondary("None")
                        .build(sender::sendMessage);
            } else {
                MessageBuilderDefaults.normal().space()
                        .tertiary("-").space()
                        .primary("Permissions:").space()
                        .build(sender::sendMessage);

                rank.getPermissions().forEach(permission -> {
                    MessageBuilderDefaults.normal().space(2)
                            .tertiary("-").space()
                            .primary(permission)
                            .build(sender::sendMessage);
                });
            }

            if (rank.getInherits().isEmpty()) {
                MessageBuilderDefaults.normal().space()
                        .tertiary("-").space()
                        .primary("Inherits:").space()
                        .secondary("None")
                        .build(sender::sendMessage);
            } else {
                MessageBuilderDefaults.normal().space()
                        .tertiary("-").space()
                        .primary("Inherits:")
                        .build(sender::sendMessage);
                rank.getInherits().forEach(inherit -> {
                    MessageBuilderDefaults.normal().space(2)
                            .tertiary("-").space()
                            .secondary(inherit)
                            .build(sender::sendMessage);
                });
            }

            sender.sendMessage(" ");
        });
    }
}
