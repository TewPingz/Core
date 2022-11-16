package me.tewpingz.core.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.profile.punishment.event.PunishmentAddEvent;
import me.tewpingz.core.profile.punishment.event.PunishmentRemoveEvent;
import me.tewpingz.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.Date;

public class PunishmentBridgeListener {

    private final TextChannel textChannel;

    public PunishmentBridgeListener(TextChannel textChannel) {
        this.textChannel = textChannel;
        Core.getInstance().getBridge().registerListener(PunishmentAddEvent.class, this::handlePunishmentAdd);
        Core.getInstance().getBridge().registerListener(PunishmentRemoveEvent.class, this::handlePunishmentRemove);
    }

    private void handlePunishmentAdd(CharSequence channel, PunishmentAddEvent event) {
        String duration = event.getPunishment().getDuration() == -1 ? "Permanently"
                : TimeUtil.formatLongIntoDetailedString(event.getPunishment().getDuration());

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Punishment Log (Added)")
                .addField("Executor", event.getExecutorName(), true)
                .addField("Target", Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName(), true)
                .addField("Punishment type", event.getPunishment().getPunishmentType().name(), true)
                .addField("Duration", duration, true)
                .addField("Reason", event.getPunishment().getReason(), true)
                .setTimestamp(new Date().toInstant())
                .setColor(Color.ORANGE.getRGB())
                .build();

        this.textChannel.sendMessageEmbeds(embed).queue();
    }

    private void handlePunishmentRemove(CharSequence channel, PunishmentRemoveEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Punishment Log (Remove)")
                .addField("Executor", event.getExecutorName(), true)
                .addField("Target", Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName(), true)
                .addField("Punishment type", event.getExpiredPunishment().getPunishment().getPunishmentType().name(), true)
                .addField("Reason", event.getExpiredPunishment().getRemovedFor(), true)
                .setTimestamp(new Date().toInstant())
                .setColor(Color.ORANGE.getRGB())
                .build();

        this.textChannel.sendMessageEmbeds(embed).queue();
    }
}
