package me.tewpingz.core;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import me.tewpingz.core.command.ListCommand;
import me.tewpingz.core.profile.ProfileListener;
import me.tewpingz.core.profile.grant.*;
import me.tewpingz.core.rank.*;
import me.tewpingz.core.rank.grant.*;
import me.tewpingz.core.util.duration.DurationContextResolver;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.core.util.uuid.AsyncUuidCommandCompletion;
import me.tewpingz.core.util.uuid.AsyncUuidContextResolver;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

@Getter
public class CorePlugin extends JavaPlugin {

    @Getter
    private static CorePlugin instance;

    private Gson gson;
    private Core core;

    private GrantScheduleManager grantScheduleManager;

    @Override
    public void onEnable() {
        instance = this;

        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .create();
        this.core = new Core(this.gson);

        this.grantScheduleManager = new GrantScheduleManager();

        FastInvManager.register(this);
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        // Register contexts
        commandManager.getCommandContexts().registerContext(AsyncUuid.class, new AsyncUuidContextResolver());
        commandManager.getCommandContexts().registerContext(Duration.class, new DurationContextResolver());
        commandManager.getCommandContexts().registerContext(Rank.RankSnapshot.class, new RankContextResolver());

        // Register command completions
        commandManager.getCommandCompletions().registerCompletion("players", new AsyncUuidCommandCompletion());
        commandManager.getCommandCompletions().registerAsyncCompletion("ranks", new RankCommandCompletion());

        // Register commands
        commandManager.registerCommand(new RankCommand());
        commandManager.registerCommand(new RanksCommand());
        commandManager.registerCommand(new GrantCommand());
        commandManager.registerCommand(new GrantsCommand());
        commandManager.registerCommand(new ListCommand());
    }

    private void registerListeners() {
        // Profile listener
        this.getServer().getPluginManager().registerEvents(new ProfileListener(this.core.getUuidManager(), this.core.getProfileManager()), this);

        // Ranks and grants listeners
        new RankBridgeListener(this);
        new GrantBridgeListener(this);
        this.getServer().getPluginManager().registerEvents(new GrantListener(this.grantScheduleManager), this);
    }
}