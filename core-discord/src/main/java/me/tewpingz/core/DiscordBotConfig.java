package me.tewpingz.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscordBotConfig {

    private String apiKey = "", grantLogChannelId = "", punishmentLogChannelId = "", chatLogChannelId = "";
    private List<SynchronizedChannelEntry> synchronizedChannels = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SynchronizedChannelEntry {
        private String serverId, channelId, webhookUrl;
    }

    public static DiscordBotConfig getConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "discord.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), DiscordBotConfig.class);
            } else {
                DiscordBotConfig config = new DiscordBotConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DiscordBotConfig();
    }
}
