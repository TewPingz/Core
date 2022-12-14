package me.tewpingz.core.listener;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.tewpingz.core.Core;
import me.tewpingz.core.DiscordBot;
import me.tewpingz.core.DiscordBotConfig;
import me.tewpingz.core.chat.DiscordChatEvent;
import me.tewpingz.core.chat.ServerChatEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ChatBridgeListener extends ListenerAdapter {

    private final DiscordBot instance;
    private final Map<String, DiscordBotConfig.SynchronizedChannelEntry> serverIdToEntry;
    private final Map<String, DiscordBotConfig.SynchronizedChannelEntry> channelIdToEntry;

    public ChatBridgeListener(DiscordBot instance) {
        this.instance = instance;
        this.serverIdToEntry = new HashMap<>();
        this.channelIdToEntry = new HashMap<>();
        this.instance.getConfig().getSynchronizedChannels().forEach(entry -> this.serverIdToEntry.put(entry.getServerId().toLowerCase(), entry));
        this.instance.getConfig().getSynchronizedChannels().forEach(entry -> this.channelIdToEntry.put(entry.getChannelId().toLowerCase(), entry));
        this.instance.getJda().addEventListener(this);
        Core.getInstance().getBridge().registerListener(ServerChatEvent.class, this::handleServerChat);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check if the message is from a user
        if (event.isWebhookMessage() || !event.isFromGuild() || event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        // Check if an entry exists
        DiscordBotConfig.SynchronizedChannelEntry entry = this.channelIdToEntry.get(event.getGuildChannel().getId());

        // If the entry doesn't exist then ignore it as it doesn't need to be synced
        if (entry == null) {
            return;
        }

        // Call an event so Minecraft can listen to it and then broadcast it
        Core.getInstance().getBridge().callEvent(new DiscordChatEvent(entry.getServerId(), event.getAuthor().getAsTag(), event.getMessage().getContentRaw()));
    }

    private void handleServerChat(CharSequence channel, ServerChatEvent event) {
        DiscordBotConfig.SynchronizedChannelEntry entry = this.serverIdToEntry.get(event.getServerId().toLowerCase());
        String webhookUrl = entry == null ? this.instance.getConfig().getChatLogWebhookUrl() : entry.getWebhookUrl();
        this.logMessage(webhookUrl, event);
    }

    private void logMessage(String webhookUrl, ServerChatEvent event) {
        WebhookClient client = WebhookClient.withUrl(webhookUrl);
        WebhookMessage webhookMessage = new WebhookMessageBuilder()
                .setAvatarUrl("https://crafatar.com/avatars/%s".formatted(event.getPlayerId().toString()))
                .setUsername("%s - %s".formatted(event.getUsername(), event.getServerId()))
                .setContent(event.getMessage())
                .setAllowedMentions(AllowedMentions.none())
                .build();
        client.send(webhookMessage);
        client.close();
    }
}
