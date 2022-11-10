package me.tewpingz.core;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.tewpingz.core.profile.ProfileListener;
import me.tewpingz.core.rank.*;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CorePlugin extends JavaPlugin {

    @Getter
    private static CorePlugin instance;

    private Gson gson;
    private Core core;

    @Override
    public void onEnable() {
        instance = this;

        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .create();
        this.core = new Core(this.gson);
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandContexts().registerContext(Rank.RankSnapshot.class, new RankContextResolver());
        commandManager.getCommandCompletions().registerAsyncCompletion("ranks", new RankCommandCompletion());
        commandManager.registerCommand(new RankCommand());
        commandManager.registerCommand(new RanksCommand());
    }

    private void registerListeners() {
        new RankBridgeListener(this);
        this.getServer().getPluginManager().registerEvents(new ProfileListener(this.core.getProfileManager()), this);
    }
}