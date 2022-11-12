package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.util.ItemBuilder;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.core.util.inventory.PaginatedInv;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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

@CommandAlias("punishments|history|h")
@CommandPermission("core.punishments")
public class PunishmentsCommand extends BaseCommand {

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onCommand(Player player, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuid(player, uuid -> {
            Profile profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () ->
                    new PunishmentsInventory(profile.getSnapshot(), asyncUuid.getName()).open(player));
        });
    }

    private static class PunishmentsInventory extends PaginatedInv {
        public PunishmentsInventory(Profile.ProfileSnapshot snapshot, String name) {
            super("Punishments for " + (snapshot.getLastSeenName().isEmpty() ? name : snapshot.getLastSeenName()));

            for (Punishment punishment : snapshot.getSortedActivePunishments()) {
                String addedAt = new Date(punishment.getStartTimestamp()).toString();

                String duration = punishment.isInfinite() ? "Permanent" :
                        TimeUtil.formatLongIntoDetailedString(punishment.getDuration());

                String expires = punishment.isInfinite() ? "Never" :
                        (punishment.hasExpired() ? "This punishment has already expired. This will be moved once they logon!"
                                : TimeUtil.formatLongIntoDetailedString(punishment.getTimeLeft()));


                ItemStack itemStack = new ItemBuilder(Material.ANVIL)
                        .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                        .enchants(Enchantment.ARROW_INFINITE)
                        .name(Component.text(punishment.getPunishmentType().name()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .lore("")
                        .lore("&6Added by&f: %s".formatted(punishment.getExecutor()))
                        .lore("&6Added on&f: %s".formatted(addedAt))
                        .lore("&6Reason&f: %s".formatted(punishment.getReason()))
                        .lore("")
                        .lore("&6Duration&f %s".formatted(duration))
                        .lore("&6Expires in&f: %s".formatted(expires))
                        .lore("")
                        .lore("&aThis is currently an active punishment.")
                        .lore("&aRight click to remove.")
                        .lore("")
                        .build();

                this.addItem(itemStack, event -> {
                    if (event.isRightClick()) {
                        new Conversation(CorePlugin.getInstance(), (Player)event.getWhoClicked(), new RemovePunishment(snapshot, punishment)).begin();
                        event.getInventory().close();
                    }
                    event.setCancelled(true);
                });
            }

            for (Punishment.ExpiredPunishment expiredPunishment : snapshot.getSortedExpiredPunishments()) {
                String addedAt = new Date(expiredPunishment.getPunishment().getStartTimestamp()).toString();

                String duration = expiredPunishment.getPunishment().isInfinite() ? "Permanent" :
                        TimeUtil.formatLongIntoDetailedString(expiredPunishment.getPunishment().getDuration());

                String removedAt = new Date(expiredPunishment.getRemovedAt()).toString();

                ItemStack itemStack = new ItemBuilder(Material.CHIPPED_ANVIL)
                        .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
                        .name(Component.text(expiredPunishment.getPunishment().getPunishmentType().name()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .lore("")
                        .lore("&6Added by&f: %s".formatted(expiredPunishment.getPunishment().getExecutor()))
                        .lore("&6Added on&f: %s".formatted(addedAt))
                        .lore("&6Reason&f: %s".formatted(expiredPunishment.getPunishment().getReason()))
                        .lore("&6Duration&f %s".formatted(duration))
                        .lore("")
                        .lore("&6Removed by&f: %s".formatted(expiredPunishment.getRemovedBy()))
                        .lore("&6Removed for&f: %s".formatted(expiredPunishment.getRemovedFor()))
                        .lore("&6Removed at&f: %s".formatted(removedAt))
                        .lore("")
                        .build();

                this.addItem(itemStack, event -> event.setCancelled(true));
            }
        }
    }

    @RequiredArgsConstructor
    private static class RemovePunishment extends StringPrompt {

        private final Profile.ProfileSnapshot snapshot;
        private final Punishment punishment;

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
                profile.removePunishment(punishment, ((Player)context.getForWhom()).getName(), input);
            });
            return null;
        }
    }
}
