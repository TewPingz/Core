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
        if (event.isWebhookMessage()) {
            return;
        }

        if (!event.isFromGuild()) {
            return;
        }

        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        DiscordBotConfig.SynchronizedChannelEntry entry = this.channelIdToEntry.get(event.getGuildChannel().getId());

        if (entry == null) {
            return;
        }

        Core.getInstance().getBridge().callEvent(new DiscordChatEvent(entry.getServerId(), event.getAuthor().getAsTag(), event.getMessage().getContentRaw()));
    }

    private void handleServerChat(CharSequence channel, ServerChatEvent event) {
        DiscordBotConfig.SynchronizedChannelEntry entry = this.serverIdToEntry.get(event.getServerId().toLowerCase());

        if (entry == null) {
            return;
        }

        WebhookClient client = WebhookClient.withUrl(entry.getWebhookUrl());
        WebhookMessage webhookMessage = new WebhookMessageBuilder()
                .setAvatarUrl("https://crafatar.com/avatars/%s".formatted(event.getPlayerId().toString()))
                .setUsername(event.getUsername())
                .setContent(event.getMessage())
                .setAllowedMentions(AllowedMentions.none())
                .build();
        client.send(webhookMessage);
        client.close();
    }
}
