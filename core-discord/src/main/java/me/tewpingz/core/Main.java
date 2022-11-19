package me.tewpingz.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
        Core core = new Core(gson, new File("."));

        DiscordBotConfig discordBotConfig = DiscordBotConfig.getConfig(new File("."));

        if (discordBotConfig.getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API Key is not set!");
        }

        new DiscordBot(discordBotConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(core::shutdown));
    }
}