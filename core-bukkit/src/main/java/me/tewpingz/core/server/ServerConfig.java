package me.tewpingz.core.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tewpingz.core.Core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

    private String serverId = "server-01", serverName = "Server 1";

    public static ServerConfig getServerConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "server.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), ServerConfig.class);
            } else {
                ServerConfig config = new ServerConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ServerConfig();
    }
}
