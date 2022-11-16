package me.tewpingz.core.command;

import me.tewpingz.core.DiscordCore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.redisson.client.protocol.CommandData;

import java.util.HashMap;
import java.util.Map;

public class CommandManager extends ListenerAdapter {

    private final JDA jda;
    private final Map<String, Command> registeredCommands;

    public CommandManager(DiscordCore discordCore) {
        this.registeredCommands = new HashMap<>();
        this.jda = discordCore.getJda();
        this.jda.addEventListener(this);
    }

    public void registerCommand(Command command) {
        this.registeredCommands.put(command.getName().toLowerCase(), command);
    }

    public void upsertCommands() {
        CommandListUpdateAction action = this.jda.updateCommands();
        for (Command command : this.registeredCommands.values()) {
            CommandDataImpl commandData = new CommandDataImpl(command.getName(), command.getDescription());
            command.getOptionEntries().forEach(commandOptionEntry ->
                    commandData.addOption(commandOptionEntry.getType(), commandOptionEntry.getName(), commandOptionEntry.getDescription(), commandOptionEntry.isRequired()));
            action = action.addCommands(commandData);
        }
        action.queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = this.registeredCommands.get(event.getName().toLowerCase());
        if (command == null) {
            return;
        }
        command.onCommand(event.getUser(), event.getGuild(), event);
    }
}
