package me.tewpingz.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderColor;
import me.tewpingz.message.MessageBuilderColorPallet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreServerInfoConfig {

    private String appealUrl = "https://www.tewpingz.me";
    private String chatPrefix = "";

    private ColorPallet defaultPallet = new ColorPallet("#FFAA00", "#FFFFFF", "#AAAAAA");
    private ColorPallet successPallet = new ColorPallet("#55FF55", "#FFFFFF", "#AAAAAA");
    private ColorPallet errorPallet = new ColorPallet("#FF5555", "#AA0000", "#AAAAAA");

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorPallet {
        private String primaryHex;
        private String secondaryHex;
        private String tertiaryHex;

        public MessageBuilder toBuilder() {
            return this.toBuilder(true);
        }

        public MessageBuilder toBuilder(boolean prefix) {
            MessageBuilderColor primary = new MessageBuilderColor(this.primaryHex);
            MessageBuilderColor secondary = new MessageBuilderColor(this.secondaryHex);
            MessageBuilderColor tertiary = new MessageBuilderColor(this.tertiaryHex);
            MessageBuilderColorPallet pallet = new MessageBuilderColorPallet(primary, secondary, tertiary);
            MessageBuilder messageBuilder = new MessageBuilder(pallet);
            if (prefix) {
                messageBuilder.primary(Core.getInstance().getConfig().getChatPrefix().replace('&', '§'));
            }
            return messageBuilder;
        }
    }

    public static CoreServerInfoConfig getConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "config.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), CoreServerInfoConfig.class);
            } else {
                CoreServerInfoConfig config = new CoreServerInfoConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CoreServerInfoConfig();
    }
}
