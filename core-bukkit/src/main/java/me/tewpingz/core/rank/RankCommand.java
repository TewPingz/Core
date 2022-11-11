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
    public static void onCreate(CommandSender commandSender, String rankId) {
        RankManager rankManager = Core.getInstance().getRankManager();

        if (rankManager.getRank(rankId) != null) {
            String message = MessageBuilderDefaults.error()
                    .primary("There is already a rank that is named")
                    .secondary(rankId)
                    .tertiary("(You can check the existing ranks using /ranks)")
                    .build();
            commandSender.sendMessage(message);
            return;
        }

        rankManager.getRealValueAsync(rankId).thenRun(() -> {
            String message = MessageBuilderDefaults.success()
                    .primary("You have successfully created a rank named")
                    .secondary(rankId)
                    .build();
            commandSender.sendMessage(message);
            Core.getInstance().getBridge().callEvent(new RankCreateEvent(commandSender.getName(), rankId));
        });
    }

    @Subcommand("setpriority")
    @Description("Set a ranks priority")
    @CommandPermission("core.rank.setpriority")
    @Syntax("<rank> <priority>")
    @CommandCompletion("@ranks")
    public void onSetPriority(CommandSender sender, Rank.RankSnapshot rankSnapshot, int priority) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setPriority(priority))
                .thenRun(() -> {
                    sender.sendMessage(MessageBuilderDefaults.success()
                            .primary("You have successfully updated the priority of")
                            .secondary(rankSnapshot.getDisplayName()).primary("to")
                            .secondary(String.valueOf(priority))
                            .build());
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
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .primary("The rank priority of")
                    .secondary(realRank.getRankId())
                    .primary("is")
                    .secondary(String.valueOf(realRank.getPriority()))
                    .build());
        });
    }

    @Subcommand("setprefix")
    @Description("Set a ranks prefix")
    @CommandPermission("core.rank.setprefix")
    @Syntax("<rank> <prefix>")
    @CommandCompletion("@ranks")
    public void onSetPrefix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String prefix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix)))
                .thenRun(() -> {
                    sender.sendMessage(MessageBuilderDefaults.success()
                            .primary("You have successfully updated the prefix of")
                            .secondary(rankSnapshot.getDisplayName()).primary("to")
                            .secondary(ChatColor.translateAlternateColorCodes('&', prefix))
                            .build());
                    Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
                });
    }

    @Subcommand("prefix")
    @Description("Get a ranks prefix")
    @CommandPermission("core.rank.prefix")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onPrefix(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealValueAsync(rankSnapshot.getRankId()).thenAccept(realRank -> {
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .primary("The rank prefix of")
                    .secondary(realRank.getRankId())
                    .primary("is")
                    .secondary(rankSnapshot.getPrefix())
                    .build());
        });
    }

    @Subcommand("setsuffix")
    @Description("Set a ranks suffix")
    @CommandPermission("core.rank.setsuffix")
    @Syntax("<rank> <suffix>")
    @CommandCompletion("@ranks")
    public void onSetSuffix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String suffix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix)))
                .thenRun(() -> {
                    sender.sendMessage(MessageBuilderDefaults.success()
                            .primary("You have successfully updated the suffix of")
                            .secondary(rankSnapshot.getDisplayName()).primary("to")
                            .secondary(ChatColor.translateAlternateColorCodes('&', suffix))
                            .build());
                    Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
                });
    }

    @Subcommand("suffix")
    @Description("Get a ranks suffix")
    @CommandPermission("core.rank.suffix")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onSuffix(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealValueAsync(rankSnapshot.getRankId()).thenAccept(realRank -> {
            sender.sendMessage(MessageBuilderDefaults.normal()
                    .primary("The rank suffix of")
                    .secondary(realRank.getRankId())
                    .primary("is")
                    .secondary(rankSnapshot.getSuffix())
                    .build());
        });
    }

    @Subcommand("setcolor|setcolour")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <color|&7&6>")
    @CommandCompletion("@ranks")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, String color) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealValueAsync(rankSnapshot.getRankId(), realRank -> realRank.setColor(ChatColor.translateAlternateColorCodes('&', color)))
                .thenRun(() -> {
                    sender.sendMessage(MessageBuilderDefaults.success()
                            .primary("You have successfully updated the color of")
                            .secondary(rankSnapshot.getDisplayName()).primary("to")
                            .secondary(color)
                            .build());
                    Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
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
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
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
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rankSnapshot.getRankId()));
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
                    .secondary(realRank.getDisplayName())
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
