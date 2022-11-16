package me.tewpingz.core;

import lombok.Getter;
import me.tewpingz.core.command.CommandManager;
import me.tewpingz.core.command.impl.SyncCommand;
import me.tewpingz.core.listener.ChatBridgeListener;
import me.tewpingz.core.listener.GrantBridgeListener;
import me.tewpingz.core.listener.PunishmentBridgeListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
public class DiscordBot {

    private final JDA jda;
    private final CommandManager commandManager;
    private final DiscordBotConfig config;

    public DiscordBot(DiscordBotConfig config) throws InterruptedException {
        this.config = config;
        this.jda = JDABuilder.createDefault(this.config.getApiKey())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        this.jda.awaitReady();

        new PunishmentBridgeListener(this.jda.getTextChannelById(this.config.getPunishmentLogChannelId()));
        new GrantBridgeListener(this.jda.getTextChannelById(this.config.getGrantLogChannelId()));
        new ChatBridgeListener(this);

        this.commandManager = new CommandManager(this);
        this.commandManager.registerCommand(new SyncCommand());
        this.commandManager.upsertCommands();
    }
}
