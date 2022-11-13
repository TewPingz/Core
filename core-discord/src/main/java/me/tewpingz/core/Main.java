package me.tewpingz.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
        Core core = new Core(gson);

        DiscordConfig discordConfig = DiscordConfig.getConfig(new File("."));

        if (discordConfig.getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API Key is not set!");
        }

        new DiscordCore(discordConfig);
    }
}