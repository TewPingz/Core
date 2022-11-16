package me.tewpingz.core.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Command {

    private final String name, description;
    private final boolean guildOnly;

    @Getter(AccessLevel.NONE)
    private final List<CommandOptionEntry> optionEntries = new ArrayList<>();

    public void addOption(OptionType optionType, String name, String description) {
        this.addOption(optionType, name, description, true);
    }

    public void addOption(OptionType optionType, String name, String description, boolean required) {
        this.optionEntries.add(new CommandOptionEntry(optionType, name, description, required));
    }

    protected List<CommandOptionEntry> getOptionEntries() {
        return List.copyOf(this.optionEntries);
    }

    public abstract void onCommand(User user, Guild guild, SlashCommandInteractionEvent event);

    @Getter
    @RequiredArgsConstructor
    protected static class CommandOptionEntry {
        private final OptionType type;
        private final String name, description;
        private final boolean required;
    }
}
