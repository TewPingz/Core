package me.tewpingz.core.rank.grant;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.ItemBuilder;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.core.util.inventory.PaginatedInv;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@CommandAlias("grants")
@CommandPermission("core.grants")
public class GrantsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void onCommand(Player player, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuidAsync().thenAccept(uuid -> {
            if (uuid == null) {
                MessageBuilderDefaults.error()
                        .secondary(asyncUuid.getName()).space()
                        .primary("has not joined the server before!")
                        .build(player::sendMessage);
                return;
            }

            Profile profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> new GrantsInventory(profile.getSnapshot()).open(player));
        });
    }

    @RequiredArgsConstructor
    private static class RemoveGrantPrompt extends StringPrompt {

        private final Profile.ProfileSnapshot snapshot;
        private final Grant grant;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Please type a reason for removing this grant")
                    .tertiary(".")
                    .toString();
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            Core.getInstance().getProfileManager().updateRealValueAsync(snapshot.getPlayerId(), profile -> {
                profile.removeGrant(grant, ((Player)context.getForWhom()).getName(), input);
            });
            return null;
        }
    }

    private static class GrantsInventory extends PaginatedInv {
        public GrantsInventory(Profile.ProfileSnapshot snapshot) {
            super(ChatColor.GOLD + "Grants for " + snapshot.getLastSeenName());

            for (Grant activeGrant : snapshot.getSortedActiveGrants()) {
                Rank.RankSnapshot rankSnapshot = activeGrant.getRankSnapshot();

                String addedAt = new Date(activeGrant.getStartTimestamp()).toString();

                String duration = activeGrant.isInfinite() ? "Permanent" :
                        TimeUtil.formatLongIntoDetailedString(activeGrant.getDuration());

                String expires = activeGrant.isInfinite() ? "Never" :
                        TimeUtil.formatLongIntoDetailedString(activeGrant.getTimeLeft());

                ItemStack itemStack = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                        .enchants(Enchantment.ARROW_INFINITE)
                        .name(activeGrant.getRankNameComponent())
                        .color(rankSnapshot == null ? NamedTextColor.WHITE : rankSnapshot.getColor().toTextColor())
                        .lore("")
                        .lore("&6Added by&f: %s".formatted(activeGrant.getExecutor()))
                        .lore("&6Added on&f: %s".formatted(addedAt))
                        .lore("&6Reason&f: %s".formatted(activeGrant.getReason()))
                        .lore("")
                        .lore("&6Duration&f %s".formatted(duration))
                        .lore("&6Expires in&f: %s".formatted(expires))
                        .lore("")
                        .lore("&aThis is currently an active grant.")
                        .build();

                this.addItem(itemStack, event -> {
                    new Conversation(CorePlugin.getInstance(), (Player)event.getWhoClicked(), new RemoveGrantPrompt(snapshot, activeGrant)).begin();
                    event.setCancelled(true);
                    event.getInventory().close();
                });
            }

            for (Grant.ExpiredGrant expiredGrant : snapshot.getSortedExpiredGrants()) {
                Rank.RankSnapshot rankSnapshot = expiredGrant.getGrant().getRankSnapshot();

                String addedAt = new Date(expiredGrant.getGrant().getStartTimestamp()).toString();

                String duration = expiredGrant.getGrant().isInfinite() ? "Permanent" :
                        TimeUtil.formatLongIntoDetailedString(expiredGrant.getGrant().getDuration());

                String removedAt = new Date(expiredGrant.getRemovedAt()).toString();

                ItemStack itemStack = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
                        .name(expiredGrant.getGrant().getRankNameComponent())
                        .color(rankSnapshot == null ? NamedTextColor.WHITE : rankSnapshot.getColor().toTextColor())
                        .lore("")
                        .lore("&6Added by&f: %s".formatted(expiredGrant.getGrant().getExecutor()))
                        .lore("&6Added on&f: %s".formatted(addedAt))
                        .lore("&6Reason&f: %s".formatted(expiredGrant.getGrant().getReason()))
                        .lore("&6Duration&f %s".formatted(duration))
                        .lore("")
                        .lore("&6Removed by&f: %s".formatted(expiredGrant.getRemovedBy()))
                        .lore("&6Removed for&f: %s".formatted(expiredGrant.getRemovedFor()))
                        .lore("&6Removed at&f: %s".formatted(removedAt))
                        .lore("")
                        .lore("&aThis is currently an active grant.")
                        .build();

                this.addItem(itemStack, event -> {
                    event.setCancelled(true);
                    event.getInventory().close();
                });
            }
        }
    }
}