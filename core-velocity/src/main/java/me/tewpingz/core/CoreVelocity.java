package me.tewpingz.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.tewpingz.core.queue.Queue;
import me.tewpingz.core.queue.event.QueuePollEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "core-velocity",
        name = "Core Velocity",
        version = "1.0-SNAPSHOT",
        url = "https://www.tewpingz.me",
        authors = {"TewPingz"}
)
public class CoreVelocity {

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private Logger logger;

    private Core core;
    private Gson gson;

    private ScheduledFuture<?> thread;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
        this.core = new Core(this.gson);
        this.registerQueueTask();
        this.registerQueueListener();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.thread.cancel(false);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        this.core.getQueueManager().getQueueByPlayer(event.getPlayer().getUniqueId()).ifPresent(queue -> {
            this.core.getQueueManager().updateQueue(queue.getServerId(), realQueue -> {
                realQueue.removePlayer(event.getPlayer().getUniqueId());
            });
        });
    }

    private void registerQueueTask() {
        this.thread = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Queue.QueueSnapshot queue : this.core.getQueueManager().getQueues()) {
                for (Queue.QueuePlayer queuePlayer : queue.getQueuePlayers()) {
                    Optional<Player> optional = this.proxyServer.getPlayer(queuePlayer.getUuid());

                    if (optional.isEmpty()) {
                        continue;
                    }

                    Player player = optional.get();
                    int position = queue.getPosition(player.getUniqueId());
                    MessageBuilderDefaults.normal().primary("You are currently in queue for").space()
                            .secondary(queue.getServerId()).space()
                            .primary("in the position").space()
                            .tertiary("#").secondary(position + 1).tertiary(".")
                            .build(player::sendMessage);
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void registerQueueListener() {
        this.core.getBridge().registerListener(QueuePollEvent.class, (charSequence, event) -> {
            this.core.getQueueManager().updateQueue(event.getServerId(), queue -> {
                Queue.QueuePlayer entry = queue.getFirstEntry();

                // This means queue is empty
                if (entry == null) {
                    return;
                }

                Optional<Player> optional = this.proxyServer.getPlayer(entry.getUuid());

                if (optional.isEmpty()) {
                    return;
                }

                // Remove the player from the queue
                queue.removeEntry(entry);

                // Find the server
                Player player = optional.get();
                this.proxyServer.getServer(event.getServerId().toLowerCase()).ifPresent(registeredServer -> {
                    player.createConnectionRequest(registeredServer).connect().thenAccept(result -> {
                        if (result.isSuccessful()) {
                            MessageBuilderDefaults.success()
                                    .primary("You have been successfully sent to").space()
                                    .secondary(registeredServer.getServerInfo().getName()).tertiary(".")
                                    .build(player::sendMessage);
                        } else {
                            MessageBuilderDefaults.error()
                                    .primary("The transfer to").space()
                                    .secondary(registeredServer.getServerInfo().getName()).space()
                                    .primary("was unsuccessful").tertiary(".")
                                    .build(player::sendMessage);
                        }
                    });
                });
            });
        });
    }
}
