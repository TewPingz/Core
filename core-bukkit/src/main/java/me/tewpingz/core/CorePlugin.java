package me.tewpingz.core;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import me.tewpingz.core.command.AltsCommand;
import me.tewpingz.core.command.ListCommand;
import me.tewpingz.core.command.SetMaxCommand;
import me.tewpingz.core.profile.ProfileListener;
import me.tewpingz.core.profile.grant.GrantScheduleManager;
import me.tewpingz.core.profile.grant.command.GrantCommand;
import me.tewpingz.core.profile.grant.command.GrantsCommand;
import me.tewpingz.core.profile.grant.listener.GrantBridgeListener;
import me.tewpingz.core.profile.grant.listener.GrantListener;
import me.tewpingz.core.profile.punishment.PunishmentScheduleManager;
import me.tewpingz.core.profile.punishment.command.*;
import me.tewpingz.core.profile.punishment.listener.PunishmentBridgeListener;
import me.tewpingz.core.profile.punishment.listener.PunishmentListener;
import me.tewpingz.core.rank.*;
import me.tewpingz.core.server.ServerInitializer;
import me.tewpingz.core.server.listener.ServerBridgeListener;
import me.tewpingz.core.server.listener.ServerListener;
import me.tewpingz.core.util.duration.DurationCommandCompletion;
import me.tewpingz.core.util.duration.DurationContextResolver;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.core.util.uuid.AsyncUuidCommandCompletion;
import me.tewpingz.core.util.uuid.AsyncUuidContextResolver;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

@Getter
public class CorePlugin extends JavaPlugin {

    @Getter
    private static CorePlugin instance;

    private Gson gson;
    private Core core;

    private ServerInitializer serverInitializer;
    private GrantScheduleManager grantScheduleManager;
    private PunishmentScheduleManager punishmentScheduleManager;

    @Override
    public void onEnable() {
        instance = this;
        FastInvManager.register(this);

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        this.gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
        this.core = new Core(this.gson);

        this.serverInitializer = new ServerInitializer(this);
        this.grantScheduleManager = new GrantScheduleManager();
        this.punishmentScheduleManager = new PunishmentScheduleManager();

        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        this.serverInitializer.shutdown();
        this.core.shutdown();
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
        commandManager.getCommandCompletions().registerAsyncCompletion("duration", new DurationCommandCompletion());

        // Register commands
        commandManager.registerCommand(new RankCommand());
        commandManager.registerCommand(new RanksCommand());
        commandManager.registerCommand(new GrantCommand());
        commandManager.registerCommand(new GrantsCommand());
        commandManager.registerCommand(new ListCommand());
        commandManager.registerCommand(new BanCommand());
        commandManager.registerCommand(new BlacklistCommand());
        commandManager.registerCommand(new MuteCommand());
        commandManager.registerCommand(new AltsCommand());
        commandManager.registerCommand(new PunishmentsCommand());
        commandManager.registerCommand(new PunishCommand());
        commandManager.registerCommand(new UnbanCommand());
        commandManager.registerCommand(new UnblacklistCommand());
        commandManager.registerCommand(new UnmuteCommand());
        commandManager.registerCommand(new SetMaxCommand());
    }

    private void registerListeners() {
        // Bridge listeners
        new RankBridgeListener(this);
        new GrantBridgeListener(this);
        new PunishmentBridgeListener(this);
        new ServerBridgeListener(this);

        // Bukkit listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new GrantListener(this.grantScheduleManager), this);
        pluginManager.registerEvents(new PunishmentListener(this.punishmentScheduleManager), this);
        pluginManager.registerEvents(new ProfileListener(this.core.getUuidManager(), this.core.getProfileManager()), this);
        pluginManager.registerEvents(new ServerListener(this.serverInitializer), this);
    }
}