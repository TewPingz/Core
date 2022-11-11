package me.tewpingz.core.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
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
                    .primary("There is already a rank that is named")
                    .secondary(rankName)
                    .tertiary("(You can check the existing ranks using /ranks)")
                    .build(commandSender::sendMessage);
            return;
        }

        rankManager.updateRealValueAsync(rankName.toLowerCase(), rank -> {
            rank.setDisplayName(rankName);
        }).thenRun(() -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully created a rank named")
                    .secondary(rankName)
                    .build(commandSender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankCreateEvent(commandSender.getName(), rankName));
        });
    }

    @Subcommand("setpriority")
    @Description("Set a ranks priority")
    @CommandPermission("core.rank.setpriority")
    @Syntax("<rank> <priority>")
    @CommandCompletion("@ranks")
    public void onSetPriority(CommandSender sender, Rank.RankSnapshot rankSnapshot, int priority) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setPriority(priority)).thenRun(() -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the priority of")
                    .secondary(rankSnapshot.getDisplayName()).primary("to")
                    .secondary(String.valueOf(priority))
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
        });
    }

    @Subcommand("priority")
    @Description("Get a ranks priority")
    @CommandPermission("core.rank.priority")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onPriority(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealValueAsync(rankSnapshot.getRankId()).thenAccept(realRank -> {
            MessageBuilderDefaults.normal()
                    .primary("The rank priority of")
                    .secondary(realRank.getRankId())
                    .primary("is")
                    .secondary(String.valueOf(realRank.getPriority()))
                    .build(sender::sendMessage);
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
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setPrefix(translatedPrefix)).thenRun(() -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the prefix of")
                    .secondary(rankSnapshot.getDisplayName())
                    .primary("to")
                    .secondary(prefix)
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getDisplayName()));
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
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setSuffix(translatedSuffix)).thenRun(() -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the suffix of")
                    .secondary(rankSnapshot.getDisplayName()).primary("to")
                    .secondary(ChatColor.translateAlternateColorCodes('&', suffix))
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getDisplayName()));
        });
    }

    @Subcommand("setcolor|setcolour|color|colour")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <color|&7&6>")
    @CommandCompletion("@ranks")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, String color) {
        RankManager rankManager = Core.getInstance().getRankManager();
        String translatedColor = ChatColor.translateAlternateColorCodes('&', color);
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setColor(translatedColor)).thenRun(() -> {
            sender.sendMessage(MessageBuilderDefaults.success()
                    .primary("You have successfully updated the color of")
                    .secondary(rankSnapshot.getDisplayName()).primary("to")
                    .secondary(color)
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getDisplayName()));
        });
    }

    @Subcommand("permission")
    @Description("Add/Remove a ranks permission")
    @CommandPermission("core.rank.permission")
    @Syntax("<rank> <permission>")
    @CommandCompletion("@ranks")
    public void onPermission(CommandSender sender, Rank.RankSnapshot rankSnapshot, @Single String permission) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueWithFunctionAsync(rankSnapshot.getRankId(), realRank -> {
            if (realRank.getPermissions().contains(permission)) {
                realRank.getPermissions().remove(permission);
                return false;
            } else {
                realRank.getPermissions().add(permission);
                return true;
            }
        }).thenAccept(added -> {
            sender.sendMessage(MessageBuilderDefaults.success()
                    .primary("You have successfully")
                    .secondary(added ? "added" : "removed")
                    .secondary(permission)
                    .primary("to")
                    .secondary(rankSnapshot.getDisplayName())
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getDisplayName()));
        });
    }

    @Subcommand("inherit")
    @Description("Add/Remove a ranks inherit")
    @CommandPermission("core.rank.inherit")
    @Syntax("<rank> <inheritRank>")
    @CommandCompletion("@ranks")
    public void onInherit(CommandSender sender, Rank.RankSnapshot rankSnapshot, Rank.RankSnapshot inheritRankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueWithFunctionAsync(rankSnapshot.getRankId(), realRank -> {
            if (realRank.getInherits().contains(inheritRankSnapshot.getRankId())) {
                realRank.getInherits().remove(inheritRankSnapshot.getRankId());
                return false;
            } else {
                realRank.getInherits().add(inheritRankSnapshot.getRankId());
                return true;
            }
        }).thenAccept(added -> {
            sender.sendMessage(MessageBuilderDefaults.success()
                    .primary("You have successfully")
                    .secondary(added ? "added" : "removed")
                    .secondary(inheritRankSnapshot.getDisplayName())
                    .primary("as an inherit to")
                    .secondary(rankSnapshot.getDisplayName())
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getDisplayName()));
        });
    }

    @Subcommand("info")
    @Description("Check a ranks information")
    @CommandPermission("core.rank.info")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onInfo(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealValueAsync(rankSnapshot.getRankId()).thenAccept(realRank -> {
            sender.sendMessage(" ");

            sender.sendMessage(MessageBuilderDefaults.normal()
                    .primary("This is the information for")
                    .secondary(realRank.getColor() + realRank.getDisplayName())
                    .build());
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .tertiary(" -")
                    .primary("Display Name:")
                    .secondary(realRank.getDisplayName())
                    .build());
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .tertiary(" -")
                    .primary("Priority:")
                    .secondary(String.valueOf(realRank.getPriority()))
                    .build());
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .tertiary(" -")
                    .primary("Prefix:")
                    .secondary(realRank.getPrefix())
                    .build());
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .tertiary(" -")
                    .primary("Suffix:")
                    .secondary(realRank.getSuffix())
                    .build());

            if (realRank.getPermissions().isEmpty()) {
                sender.sendMessage(MessageBuilderDefaults.normal()
                        .tertiary(" -")
                        .primary("Permissions:")
                        .secondary("None")
                        .build());
            } else {
                sender.sendMessage(MessageBuilderDefaults.normal()
                        .tertiary(" -")
                        .primary("Permissions:")
                        .build());
                realRank.getPermissions().forEach(permission -> {
                    sender.sendMessage(MessageBuilderDefaults.normal()
                            .tertiary("  -")
                            .primary(permission)
                            .build());
                });
            }

            if (realRank.getInherits().isEmpty()) {
                sender.sendMessage(MessageBuilderDefaults.normal()
                        .tertiary(" -")
                        .primary("Inherits:")
                        .secondary("None")
                        .build());
            } else {
                sender.sendMessage(MessageBuilderDefaults.normal()
                        .tertiary(" -")
                        .primary(" Inherits:")
                        .build());
                realRank.getInherits().forEach(inherit -> {
                    sender.sendMessage(MessageBuilderDefaults.normal()
                            .tertiary("  -")
                            .secondary(inherit)
                            .build());
                });
            }

            sender.sendMessage(" ");
        });
    }
}
