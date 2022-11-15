package me.tewpingz.core;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreMongoConfig {

    private String host = "127.0.0.1";
    private int port = 27017;
    private String database = "core";
    private String username = "";
    private String password = "";

    public MongoClientSettings transform() {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyToClusterSettings(cluster -> cluster.hosts(List.of(new ServerAddress(this.host, this.port))));

        if (!username.isEmpty() && !password.isEmpty()) {
            MongoCredential credential = MongoCredential.createCredential(this.username, this.database, this.password.toCharArray());
            builder.credential(credential);
        }

        return builder.build();
    }

    public static CoreMongoConfig getConfig(File directory) {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        Path path = new File(directory, "mongo.json").toPath();

        try {
            if (Files.exists(path)) {
                return Core.getInstance().getGson().fromJson(Files.newBufferedReader(path), CoreMongoConfig.class);
            } else {
                CoreMongoConfig config = new CoreMongoConfig();
                Files.createFile(path);
                Files.writeString(path, Core.getInstance().getGson().toJson(config));
                return config;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CoreMongoConfig();
    }
}
