package me.tewpingz.core.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.profile.grant.event.GrantAddEvent;
import me.tewpingz.core.profile.grant.event.GrantRemoveEvent;
import me.tewpingz.core.profile.punishment.event.PunishmentAddEvent;
import me.tewpingz.core.profile.punishment.event.PunishmentRemoveEvent;
import me.tewpingz.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.Date;

public class GrantBridgeListener {
    public GrantBridgeListener(TextChannel textChannel) {
        Core.getInstance().getBridge().registerListener(GrantAddEvent.class, (charSequence, event) -> {
            String duration = event.getGrant().getDuration() == -1 ? "Permanently"
                    : TimeUtil.formatLongIntoDetailedString(event.getGrant().getDuration());

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Grant Log (Added)")
                    .addField("Executor", event.getExecutorName(), true)
                    .addField("Target", Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName(), true)
                    .addField("Rank", event.getGrant().getRankId(), true)
                    .addField("Duration", duration, true)
                    .addField("Reason", event.getGrant().getReason(), true)
                    .setTimestamp(new Date().toInstant())
                    .setColor(Color.ORANGE.getRGB())
                    .build();

            textChannel.sendMessageEmbeds(embed).queue();
        });

        Core.getInstance().getBridge().registerListener(GrantRemoveEvent.class, (charSequence, event) -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Grant Log (Remove)")
                    .addField("Executor", event.getExecutorName(), true)
                    .addField("Target", Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName(), true)
                    .addField("Rank", event.getExpiredGrant().getGrant().getRankId(), true)
                    .addField("Reason", event.getExpiredGrant().getRemovedFor(), true)
                    .setTimestamp(new Date().toInstant())
                    .setColor(Color.ORANGE.getRGB())
                    .build();

            textChannel.sendMessageEmbeds(embed).queue();
        });
    }
}
