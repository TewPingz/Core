package me.tewpingz.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreRedisConfig {

    private String host = "127.0.0.1";
    private int port = 6379;
    private String username = "";
    private String password = "";

    public Config transform() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://%s:%s".formatted(this.host, this.port))
                .setUsername(this.username.isEmpty() ? null : this.username)
                .setPassword(this.password.isEmpty() ? null : this.password);
        config.setTransportMode(TransportMode.NIO);
        return config;
    }

    public static CoreRedisConfig getConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "redis.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), CoreRedisConfig.class);
            } else {
                CoreRedisConfig config = new CoreRedisConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CoreRedisConfig();
    }
}
