package me.tewpingz.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscordConfig {

    private String apiKey = "", grantLogChannelId = "", punishmentLogChannelId = "", otherChannelId = "";

    public static DiscordConfig getConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "discord.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), DiscordConfig.class);
            } else {
                DiscordConfig config = new DiscordConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DiscordConfig();
    }
}
