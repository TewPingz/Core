package me.tewpingz.core.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.tewpingz.core.Core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

    private String serverId, serverName;

    public static ServerConfig getServerConfig(File directory) {
        Path path = new File(directory, "server.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), ServerConfig.class);
            } else {
                ServerConfig config = new ServerConfig("server-01", "Server 1");
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ServerConfig("server-01", "Server 1");
    }
}
