package me.tewpingz.core;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import me.tewpingz.core.bridge.Bridge;
import me.tewpingz.core.profile.ProfileManager;
import me.tewpingz.core.profile.alt.AltManager;
import me.tewpingz.core.rank.RankManager;
import me.tewpingz.core.server.ServerManager;
import me.tewpingz.core.util.uuid.UuidManager;
import me.tewpingz.redigo.RediGo;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

@Getter
public class Core {

    @Getter
    private static Core instance;

    // Data handlers
    private final RedissonClient redissonClient;
    private final MongoClient mongoClient;
    private final Gson gson;
    private final RediGo rediGo;
    private final Bridge bridge;

    // Managers
    private final UuidManager uuidManager;
    private final RankManager rankManager;
    private final ProfileManager profileManager;
    private final AltManager altManager;
    private final ServerManager serverManager;

    public Core(Gson gson) {
        instance = this;

        this.redissonClient = Redisson.create();
        this.mongoClient = MongoClients.create();
        this.gson = gson.newBuilder().create();
        this.rediGo = new RediGo("core", this.mongoClient, this.redissonClient, this.gson);
        this.bridge = new Bridge(this);

        this.uuidManager = new UuidManager();
        this.rankManager = new RankManager(this);
        this.profileManager = new ProfileManager(this);
        this.altManager = new AltManager(this);
        this.serverManager = new ServerManager(this);
    }

    public void shutdown() {
        this.redissonClient.shutdown();
        this.mongoClient.close();
    }
}
