package me.tewpingz.core.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.UUID;

public class SyncCommandListener extends ListenerAdapter {

    public SyncCommandListener(JDA jda) {
        jda.upsertCommand("sync", "A command that lets you sync with the minecraft server")
                .addOption(OptionType.STRING, "username", "The username of your account", true)
                .addOption(OptionType.STRING, "code", "The code that the server gave you", true)
                .queue();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("sync")) {
            return;
        }

        event.deferReply(true).queue();

        String username = event.getOption("username").getAsString();
        UUID uuid = Core.getInstance().getUuidManager().getUuid(username).getUuid();

        if (uuid == null) {
            event.getHook().sendMessageEmbeds(this.getInvalid()).setEphemeral(true).queue();
            return;
        }

        String code = event.getOption("code").getAsString();
        Core.getInstance().getProfileManager().updateRealValueAsync(uuid, profile -> {
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
                .build();
    }

    private MessageEmbed getAlreadySynced() {
        return new EmbedBuilder()
                .setTitle("Discord Sync")
                .setColor(Color.RED.getRGB())
                .setDescription("Your discord account is already synced with that minecraft account.")
                .build();
    }

    private MessageEmbed getSuccess(String name) {
        return new EmbedBuilder()
                .setTitle("Discord Sync")
                .setColor(Color.GREEN.getRGB())
                .setDescription("You have successfully linked your discord account to " + name)
                .build();
    }
}
