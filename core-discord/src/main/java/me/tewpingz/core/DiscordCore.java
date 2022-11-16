package me.tewpingz.core;

import lombok.Getter;
import me.tewpingz.core.command.CommandManager;
import me.tewpingz.core.command.impl.SyncCommand;
import me.tewpingz.core.listener.GrantBridgeListener;
import me.tewpingz.core.listener.PunishmentBridgeListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Getter
public class DiscordCore {

    private final JDA jda;
    private final CommandManager commandManager;

    public DiscordCore(DiscordConfig config) throws InterruptedException {
        this.jda = JDABuilder.createDefault(config.getApiKey()).build();
        this.jda.awaitReady();

        new PunishmentBridgeListener(this.jda.getTextChannelById(config.getPunishmentLogChannelId()));
        new GrantBridgeListener(this.jda.getTextChannelById(config.getGrantLogChannelId()));

        this.commandManager = new CommandManager(this);
        this.commandManager.registerCommand(new SyncCommand());
        this.commandManager.upsertCommands();
    }
}
