package me.tewpingz.core.profile.punishment;

import lombok.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderColor;
import me.tewpingz.message.MessageBuilderColorPalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class Punishment {

    private final PunishmentType punishmentType;
    private final String executor;
    private final String reason;
    private final long startTimestamp;
    private final long duration;

    public boolean isInfinite() {
        return this.duration <= -1;
    }

    public long getExpireTimestamp() {
        return this.isInfinite() ? Long.MAX_VALUE : this.startTimestamp + this.duration;
    }

    public long getTimeLeft() {
        return this.isInfinite() ? Long.MAX_VALUE : this.getExpireTimestamp() - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return this.getTimeLeft() <= 0;
    }

    public Component formatKickMessage(boolean initial) {
        MessageBuilderColorPalette pallet = new MessageBuilderColorPalette(MessageBuilderColor.RED, MessageBuilderColor.WHITE, MessageBuilderColor.GRAY);
        MessageBuilder builder = new MessageBuilder(pallet);
        boolean permanently = this.isInfinite();

        if (this.punishmentType != PunishmentType.MUTE) {
            builder.primary(initial ? "You have been" : "You are").space()
                    .primary(this.isInfinite() ? "permanently" : "temporarily").space();

            if (!permanently) {
                builder.primary("for").space().secondary(TimeUtil.formatLongIntoDetailedString(initial ? this.getDuration() : this.getTimeLeft())).space();
            }
        }

        switch (this.punishmentType) {
            case BLACKLIST -> {
                return builder.primary("blacklisted").space()
                        .primary("from this server").tertiary("!").append(Component.newline())
                        .tertiary("Reason:").space().secondary(this.reason).append(Component.newline())
                        .tertiary("This punishment cannot be appealed!").space()
                        .build();
            }
            case MUTE -> {
                return builder.primary("You").space()
                        .primary(initial ? "have been" : "are currently").space()
                        .primary(this.isInfinite() ? "permanently" : "temporarily").space()
                        .primary("muted").space()
                        .tertiary(!permanently ? "(%s)".formatted(TimeUtil.formatLongIntoDetailedString(initial ? this.getDuration() : this.getTimeLeft())) : "")
                        .build();
            }
            case BAN -> builder.primary("banned").space();
            case IP_BAN -> builder.primary("ip-banned").space();
        }

        return builder
                .primary("from this server").tertiary("!").append(Component.newline())
                .tertiary("Reason:").space().secondary(this.reason).append(Component.newline())
                .tertiary("Find out more:").space()
                .append(Component.text(Core.getInstance().getConfig().getAppealUrl()).color(NamedTextColor.AQUA).decoration(TextDecoration.UNDERLINED, true))
                .build();
    }

    public Component formatKickRelatedMessage(boolean initial) {
        MessageBuilderColorPalette pallet = new MessageBuilderColorPalette(MessageBuilderColor.RED, MessageBuilderColor.WHITE, MessageBuilderColor.GRAY);
        MessageBuilder builder = new MessageBuilder(pallet)
                .primary(initial ? "Your account is related to an account that has just been" : "Your account is related to account that is").space()
                .primary(this.isInfinite() ? "permanently" : "temporarily").space();

        if (!this.isInfinite()) {
            builder.primary("for").space().secondary(TimeUtil.formatLongIntoDetailedString(initial ? this.getDuration() : this.getTimeLeft())).space();
        }

        switch (this.punishmentType) {
            case BLACKLIST -> {
                return builder.primary("blacklisted").space()
                        .primary("from this server").tertiary("!").append(Component.newline())
                        .tertiary("Reason:").space().secondary(this.reason).append(Component.newline())
                        .tertiary("This punishment cannot be appealed!").space()
                        .build();
            }
            case IP_BAN -> builder.primary("ip-banned").space();
        }

        return builder
                .primary("from this server").tertiary("!").append(Component.newline())
                .tertiary("Reason:").space().secondary(this.reason).append(Component.newline())
                .tertiary("Find out more:").space()
                .append(Component.text(Core.getInstance().getConfig().getAppealUrl()).color(NamedTextColor.AQUA).decoration(TextDecoration.UNDERLINED, true))
                .build();
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ExpiredPunishment {

        private final Punishment punishment;
        private final String removedBy, removedFor;
        private final long removedAt;

    }
}
