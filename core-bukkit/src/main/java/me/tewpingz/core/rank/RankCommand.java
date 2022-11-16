package me.tewpingz.core.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankDeleteEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import me.tewpingz.core.rank.event.RankUpdatePermissionEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("rank")
@CommandPermission("core.rank")
public class RankCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Syntax("[page]")
    @CommandCompletion("@empty")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Create a rank")
    @CommandPermission("core.rank.create")
    @CommandCompletion("@empty")
    @Syntax("<name>")
    public static void onCreate(CommandSender commandSender, String rankName) {
        RankManager rankManager = Core.getInstance().getRankManager();

        if (rankManager.getCachedRank(rankName) != null) {
            MessageBuilderDefaults.error()
                    .primary("There is already a rank that is named").space()
                    .secondary(rankName).space()
                    .tertiary("(You can check the existing ranks using /ranks)")
                    .build(commandSender::sendMessage);
            return;
        }

        rankManager.updateRealRankAsync(rankName.toLowerCase(), rank -> {
            rank.setDisplayName(rankName);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully created a rank named").space()
                    .append(rank.getDisplayNameWithColor())
                    .tertiary(".")
                    .build(commandSender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankCreateEvent(commandSender.getName(), rank));
        });
    }

    @Subcommand("delete")
    @Description("Delete a rank")
    @CommandPermission("core.rank.delete")
    @CommandCompletion("@ranks")
    @Syntax("<rank>")
    public static void onDelete(CommandSender commandSender, Rank.RankSnapshot rank) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.evictRankAsync(rank.getRankId()).thenRun(() -> {
            Core.getInstance().getBridge().callEvent(new RankDeleteEvent(commandSender.getName(), rank));
            MessageBuilderDefaults.success()
                    .primary("You have successfully deleted the").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .primary("rank").tertiary("!")
                    .build(commandSender::sendMessage);
        });
    }

    @Subcommand("setpriority|priority")
    @Description("Set a ranks priority")
    @CommandPermission("core.rank.setpriority")
    @Syntax("<rank> <priority>")
    @CommandCompletion("@ranks @empty")
    public void onSetPriority(CommandSender sender, Rank.RankSnapshot rankSnapshot, int priority) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setPriority(priority);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the priority of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .primary("to").space()
                    .secondary(String.valueOf(priority))
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setprefix|prefix")
    @Description("Set a ranks prefix")
    @CommandPermission("core.rank.setprefix")
    @Syntax("<rank> <prefix>")
    @CommandCompletion("@ranks @empty")
    public void onSetPrefix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String prefix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        String translatedPrefix = ChatColor.translateAlternateColorCodes('&', prefix);
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setPrefix(translatedPrefix);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the prefix of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .primary("to").space()
                    .secondary(translatedPrefix)
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setsuffix|suffix")
    @Description("Set a ranks suffix")
    @CommandPermission("core.rank.setsuffix")
    @Syntax("<rank> <suffix>")
    @CommandCompletion("@ranks @empty")
    public void onSetSuffix(CommandSender sender, Rank.RankSnapshot rankSnapshot, String suffix) {
        RankManager rankManager = Core.getInstance().getRankManager();
        String translatedSuffix = ChatColor.translateAlternateColorCodes('&', suffix);
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setSuffix(translatedSuffix);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the suffix of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .secondary(translatedSuffix)
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setdisplayname|setdp")
    @Description("Set a ranks display name")
    @CommandPermission("core.rank.setdisplayname")
    @Syntax("<rank> <displayName>")
    @CommandCompletion("@ranks @empty")
    public void onSetDisplayName(CommandSender sender, Rank.RankSnapshot rankSnapshot, String displayName) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.setDisplayName(displayName);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the suffix of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .secondary(displayName)
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setbold")
    @Description("Set a ranks display name to bold")
    @CommandPermission("core.rank.setbold")
    @Syntax("<rank> <true|false>")
    @CommandCompletion("@ranks true|false")
    public void onSetBold(CommandSender sender, Rank.RankSnapshot rankSnapshot, boolean state) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().setBold(state);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have").space()
                    .primary(state ? "bolded" : "unbloded").space()
                    .primary("the display name of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setitalic")
    @Description("Set a ranks display name to italic")
    @CommandPermission("core.rank.setitalic")
    @Syntax("<rank> <true|false>")
    @CommandCompletion("@ranks true|false")
    public void onSetItalic(CommandSender sender, Rank.RankSnapshot rankSnapshot, boolean state) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().setItalic(state);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have").space()
                    .primary(state ? "italicized" : "unitalicized").space()
                    .primary("the display name of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setcolor|setcolour|color|colour")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <red> <green> <blue>")
    @CommandCompletion("@ranks @empty")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, int red, int green, int blue) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().updateColor(red, green, blue);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the color of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }

    @Subcommand("setcolorhex|setcolourhex|colorhex|colourhex")
    @Description("Set a ranks color")
    @CommandPermission("core.rank.setcolor")
    @Syntax("<rank> <hex>")
    @CommandCompletion("@ranks @empty")
    public void onSetColor(CommandSender sender, Rank.RankSnapshot rankSnapshot, String hex) {

        if (!hex.startsWith("#")) {
            MessageBuilderDefaults.error().secondary(hex).space()
                    .primary("is not a valid hex code").tertiary("!")
                    .build(sender::sendMessage);
            return;
        }

        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
            realRank.getColor().updateColor(hex);
        }).thenAccept(rank -> {
            MessageBuilderDefaults.success()
                    .primary("You have successfully updated the color of").space()
                    .append(rank.getDisplayNameWithColor()).space()
                    .tertiary(".")
                    .build(sender::sendMessage);
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
        });
    }


    @Subcommand("permission")
    @Description("Add/Remove a ranks permission")
    @CommandPermission("core.rank.permission")
    @Syntax("<rank> <permission>")
    @CommandCompletion("@ranks @empty")
    public void onPermission(CommandSender sender, Rank.RankSnapshot rankSnapshot, @Single String permission) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
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
                    .append(rank.getDisplayNameWithColor())
                    .tertiary(".")
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
            Core.getInstance().getBridge().callEvent(new RankUpdatePermissionEvent(rank));
        });
    }

    @Subcommand("inherit")
    @Description("Add/Remove a ranks inherit")
    @CommandPermission("core.rank.inherit")
    @Syntax("<rank> <inheritRank>")
    @CommandCompletion("@ranks @ranks")
    public void onInherit(CommandSender sender, Rank.RankSnapshot rankSnapshot, Rank.RankSnapshot inherit) {
        if (inherit.getRankId().equalsIgnoreCase(rankSnapshot.getRankId())) {
            MessageBuilderDefaults.error()
                    .primary("You cannot make the rank inherit itself")
                    .build(sender::sendMessage);
            return;
        }

        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.updateRealRankAsync(rankSnapshot.getRankId(), realRank -> {
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
                    .append(inherit.getDisplayNameWithColor())
                    .primary("as an inherit to").space()
                    .append(rank.getDisplayNameWithColor())
                    .tertiary(".")
                    .build());
            Core.getInstance().getBridge().callEvent(new RankUpdateEvent(sender.getName(), rank));
            Core.getInstance().getBridge().callEvent(new RankUpdatePermissionEvent(rank));
        });
    }

    @Subcommand("info")
    @Description("Check a ranks information")
    @CommandPermission("core.rank.info")
    @Syntax("<rank>")
    @CommandCompletion("@ranks")
    public void onInfo(CommandSender sender, Rank.RankSnapshot rankSnapshot) {
        RankManager rankManager = Core.getInstance().getRankManager();
        rankManager.getRealRank(rankSnapshot.getRankId()).thenAccept(rank -> {
            sender.sendMessage(" ");

            MessageBuilderDefaults.normal()
                    .primary("This is the information for").space()
                    .append(rank.getDisplayNameWithColor())
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
