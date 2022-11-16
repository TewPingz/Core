package me.tewpingz.core.command.impl;

import me.tewpingz.core.Core;
import me.tewpingz.core.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.UUID;

public class SyncCommand extends Command {
    public SyncCommand() {
        super("sync", "A command that lets you sync with the minecraft server", true);
        this.addOption(OptionType.STRING, "username", "The accounts username");
        this.addOption(OptionType.STRING, "code", "The code provided by the server");
    }

    @Override
    public void onCommand(User user, Guild guild, SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        String username = event.getOption("username").getAsString();
        UUID uuid = Core.getInstance().getUuidManager().getUuid(username).getUuid();

        if (uuid == null) {
            event.getHook().sendMessageEmbeds(this.getInvalid()).setEphemeral(true).queue();
            return;
        }

        String code = event.getOption("code").getAsString();
        Core.getInstance().getProfileManager().updateRealProfileAsync(uuid, profile -> {
            if (profile.getDiscordId() != null && event.getUser().getId().equalsIgnoreCase(profile.getDiscordId())) {
                event.getHook().sendMessageEmbeds(this.getAlreadySynced()).setEphemeral(true).queue();
                return;
            }

            if (profile.getSyncCooldown() != -1 && profile.getSyncCooldown() < System.currentTimeMillis()) {
                event.getHook().sendMessageEmbeds(this.getInvalid()).setEphemeral(true).queue();
                return;
            }

            if (profile.getSyncCode() == null || !profile.getSyncCode().equalsIgnoreCase(code)) {
                event.getHook().sendMessageEmbeds(this.getInvalid()).setEphemeral(true).queue();
                return;
            }

            profile.setDiscordId(event.getUser().getId());
            event.getHook().sendMessageEmbeds(this.getSuccess(profile.getLastSeenName())).setEphemeral(true).queue();
            profile.setSyncCode(null);
        });
    }

    private MessageEmbed getInvalid() {
        return new EmbedBuilder()
                .setTitle("Discord Sync")
                .setColor(Color.RED.getRGB())
                .setDescription("The code that you have provided is invalid!")
                .setThumbnail("https://media.tenor.com/ldpxxfq73qIAAAAC/its-invalid-jon-taffer.gif")
                .build();
    }

    private MessageEmbed getAlreadySynced() {
        return new EmbedBuilder()
                .setTitle("Discord Sync")
                .setColor(Color.RED.getRGB())
                .setDescription("Your discord account is already synced with that minecraft account.")
                .setThumbnail("https://media.tenor.com/PxDdW7PyM5EAAAAS/curious-xavier-smith-curious.gif")
                .build();
    }

    private MessageEmbed getSuccess(String name) {
        return new EmbedBuilder()
                .setTitle("Discord Sync")
                .setColor(Color.GREEN.getRGB())
                .setDescription("You have successfully linked your discord account to " + name)
                .setThumbnail("https://media.tenor.com/1kztu9dQoKEAAAAC/okay-nice.gif")
                .build();
    }
}
