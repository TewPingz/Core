package me.tewpingz.core;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.tewpingz.core.profile.ProfileListener;
import me.tewpingz.core.rank.*;
import me.tewpingz.core.rank.grant.GrantBridgeListener;
import me.tewpingz.core.rank.grant.GrantCommand;
import me.tewpingz.core.rank.grant.GrantListener;
import me.tewpingz.core.rank.grant.GrantScheduleManager;
import me.tewpingz.core.util.duration.DurationContextResolver;
import me.tewpingz.core.util.uuid.NameToUuidCommandCompletion;
import me.tewpingz.core.util.uuid.NameToUuidContextResolver;
import me.tewpingz.core.util.uuid.UuidManager;
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

        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandContexts().registerContext(UuidManager.NameToUuidEntry.NameToUuidSnapshot.class, new NameToUuidContextResolver());
        commandManager.getCommandContexts().registerContext(Duration.class, new DurationContextResolver());
        commandManager.getCommandContexts().registerContext(Rank.RankSnapshot.class, new RankContextResolver());
        commandManager.getCommandCompletions().registerCompletion("players", new NameToUuidCommandCompletion());
        commandManager.getCommandCompletions().registerAsyncCompletion("ranks", new RankCommandCompletion());

        commandManager.registerCommand(new RankCommand());
        commandManager.registerCommand(new RanksCommand());
        commandManager.registerCommand(new GrantCommand());
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