package me.tewpingz.core.profile.punishment;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class PunishmentProcedure {

    private final UUID targetId;
    private final PunishmentType punishmentType;

    private long duration;
    private String reason;

    public void start(Player player) {
        this.promptForDuration(player);
    }

    private void promptForDuration(Player player) {
        new Conversation(CorePlugin.getInstance(), player, new DurationPrompt(this)).begin();
    }

    private void apply(Player player) {
        Core.getInstance().getProfileManager()
                .updateRealValueAsync(this.targetId, profile -> profile.addPunishment(this.punishmentType, player.getName(), this.reason, this.duration));
    }

    @RequiredArgsConstructor
    private static class DurationPrompt extends ValidatingPrompt {

        private final PunishmentProcedure procedure;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Please select the duration for this punishment")
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

    @RequiredArgsConstructor
    private static class ReasonPrompt extends StringPrompt {

        private final PunishmentProcedure procedure;

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return MessageBuilderDefaults.normal()
                    .primary("Please provide a reason for this punishment")
                    .tertiary(".")
                    .toString();
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            procedure.setReason(input);
            procedure.apply((Player) context.getForWhom());
            return null;
        }
    }
}
