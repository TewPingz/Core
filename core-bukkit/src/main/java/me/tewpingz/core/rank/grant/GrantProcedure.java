package me.tewpingz.core.rank.grant;

import fr.mrmicky.fastinv.FastInv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.ItemBuilder;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
public class GrantProcedure {

    private final UUID target;
    private Rank.RankSnapshot selectedRank;
    private String reason;
    private long duration;

    public GrantProcedure(UUID target) {
        this.target = target;
    }

    public void start(Player player) {
        this.promptRankSelect(player);
    }

    private void promptRankSelect(Player player) {
        new RankPrompt(this).open(player);
    }

    private void promptForDuration(Player player) {
        new Conversation(CorePlugin.getInstance(), player, new DurationPrompt(this)).begin();
    }

    private void apply(Player player) {
        Core.getInstance().getProfileManager()
                .updateRealValueAsync(this.target, profile -> profile.addGrant(this.selectedRank.getRankId(), player.getName(), this.reason, this.duration));
    }

    @AllArgsConstructor
    private static class ConformPrompt extends BooleanPrompt {

        private final GrantProcedure procedure;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Confirm this grant by typing").space()
                    .secondary("yes").space()
                    .primary("otherwise type").space()
                    .secondary("no").tertiary(".")
                    .toString();
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean input) {
            if (input) {
                MessageBuilderDefaults.success()
                        .primary("You have successfully applied that").space()
                        .append(this.procedure.getSelectedRank().getColor().apply(Component.text(this.procedure.getSelectedRank().getDisplayName()))).space()
                        .primary("grant").tertiary(".")
                        .build(message -> ((Player)context.getForWhom()).sendMessage(message));
                this.procedure.apply((Player) context.getForWhom());
            } else {
                MessageBuilderDefaults.success()
                        .primary("You have successfully cancelled that").space()
                        .append(this.procedure.getSelectedRank().getColor().apply(Component.text(this.procedure.getSelectedRank().getDisplayName()))).space()
                        .primary("grant").tertiary(".")
                        .build(message -> ((Player)context.getForWhom()).sendMessage(message));
            }
            return null;
        }
    }

    @AllArgsConstructor
    private static class ReasonPrompt extends StringPrompt {

        private final GrantProcedure procedure;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Please select a reason for this").space()
                    .append(this.procedure.getSelectedRank().getColor().apply(Component.text(this.procedure.getSelectedRank().getDisplayName()))).space()
                    .primary("grant")
                    .tertiary(".")
                    .toString();
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            this.procedure.setReason(input);
            return new ConformPrompt(this.procedure);
        }
    }

    @RequiredArgsConstructor
    private static class DurationPrompt extends ValidatingPrompt {

        private final GrantProcedure procedure;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Please select the duration for this").space()
                    .append(this.procedure.getSelectedRank().getColor().apply(Component.text(this.procedure.getSelectedRank().getDisplayName()))).space()
                    .primary("grant")
                    .tertiary(".")
                    .toString();
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) {
                return true;
            }
            Long millis = TimeUtil.parseTime(input);
            return millis != null;
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) {
                this.procedure.setDuration(-1);
            } else {
                this.procedure.setDuration(TimeUtil.parseTime(input));
            }
            return new ReasonPrompt(this.procedure);
        }
    }

    private static class RankPrompt extends FastInv {
        public RankPrompt(GrantProcedure procedure) {
            super(9 * 5, ChatColor.GOLD + "Select a rank");
            this.setItems(this.getBorders(), new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build(), event -> event.setCancelled(true));
            Core.getInstance().getRankManager().getSortedRanks().forEach(rankSnapshot -> {
                Component displayName = rankSnapshot.getColor().apply(Component.text(rankSnapshot.getDisplayName()));
                this.addItem(new ItemBuilder(Material.LEATHER_CHESTPLATE).color(rankSnapshot.getColor().toTextColor()).name(displayName).build(), event -> {
                    procedure.setSelectedRank(rankSnapshot);
                    event.getInventory().close();
                    procedure.promptForDuration((Player) event.getWhoClicked());
                });
            });
        }
    }
}
