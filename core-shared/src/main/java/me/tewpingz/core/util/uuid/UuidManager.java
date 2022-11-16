package me.tewpingz.core.util.uuid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.redigo.RediGoCollection;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UuidManager {

    private final UuidFetcher uuidFetcher;
    private final RediGoCollection<UuidToNameEntry.UuidToEntrySnapshot, UUID, UuidToNameEntry> uuidToNameCollection;
    private final RediGoCollection<NameToUuidEntry.NameToUuidSnapshot, String, NameToUuidEntry> nameToUuidCollection;

    public UuidManager() {
        this.uuidFetcher = new UuidFetcher();
        this.uuidToNameCollection = Core.getInstance().getRediGo().createCollection("uuid-name", UUID.class, UuidToNameEntry.class, 30, false, uuid -> {
            String name = this.uuidFetcher.fetchName(uuid);
            return new UuidToNameEntry(uuid, name);
        }, UuidToNameEntry::new);

        this.nameToUuidCollection = Core.getInstance().getRediGo().createCollection("name-uuid", String.class, NameToUuidEntry.class, 30, false, name -> {
            UUID uuid = this.uuidFetcher.fetchUuid(name);
            return new NameToUuidEntry(name, uuid);
        }, NameToUuidEntry::new);
    }

    public void beginCachingLocally(UUID playerId, String playerName) {
        this.uuidToNameCollection.beginCachingOrUpdateLocally(playerId);
        this.nameToUuidCollection.beginCachingOrUpdateLocally(playerName.toLowerCase());
    }

    public void stopCachingLocally(UUID playerId, String playerName) {
        this.uuidToNameCollection.stopCachingLocally(playerId);
        this.nameToUuidCollection.stopCachingLocally(playerName.toLowerCase());
    }

    public NameToUuidEntry.NameToUuidSnapshot getCachedUuid(String name) {
        return this.nameToUuidCollection.getCachedValued(name.toLowerCase());
    }

    public UuidToNameEntry.UuidToEntrySnapshot getCachedName(UUID uuid) {
        return this.uuidToNameCollection.getCachedValued(uuid);
    }

    public NameToUuidEntry.NameToUuidSnapshot getUuid(String name) {
        if (name.length() > 16) {
            return new NameToUuidEntry.NameToUuidSnapshot(null, name);
        }
        return this.nameToUuidCollection.getOrCreateRealValue(name.toLowerCase());
    }

    public UuidToNameEntry.UuidToEntrySnapshot getName(UUID uuid) {
        return this.uuidToNameCollection.getOrCreateRealValue(uuid);
    }

    public CompletableFuture<NameToUuidEntry.NameToUuidSnapshot> getUuidAsync(String name) {
        if (name.length() > 16) {
            return CompletableFuture.supplyAsync(() -> new NameToUuidEntry.NameToUuidSnapshot(null, name));
        }
        return this.nameToUuidCollection.getOrCreateRealValueAsync(name.toLowerCase());
    }

    public CompletableFuture<UuidToNameEntry.UuidToEntrySnapshot> getNameAsync(UUID uuid) {
        return this.uuidToNameCollection.getOrCreateRealValueAsync(uuid);
    }

    public void updateRealValues(UUID playerId, String name) {
        this.uuidToNameCollection.updateRealValue(playerId, uuidToNameEntry -> uuidToNameEntry.setName(name));
        this.nameToUuidCollection.updateRealValue(name.toLowerCase(), nameToUuidEntry -> nameToUuidEntry.setUuid(playerId));
    }

    public CompletableFuture<Void> updateRealValuesAsync(UUID playerId, String name) {
        return CompletableFuture.runAsync(() -> this.updateRealValues(playerId, name));
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class UuidToNameEntry implements RediGoObject<UUID, UuidToNameEntry.UuidToEntrySnapshot> {

        private final UUID uuid;

        @RediGoValue(key = "name")
        private String name;

        @Override
        public UUID getKey() {
            return this.uuid;
        }

        @Override
        public UuidToEntrySnapshot getSnapshot() {
            return new UuidToEntrySnapshot(this.uuid, this.name);
        }

        @Getter
        @RequiredArgsConstructor
        public static class UuidToEntrySnapshot implements Snapshot {
            private final UUID uuid;
            private final String name;
        }
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class NameToUuidEntry implements RediGoObject<String, NameToUuidEntry.NameToUuidSnapshot> {

        private final String name;

        @RediGoValue(key = "uuid")
        private UUID uuid;

        @Override
        public String getKey() {
            return this.name;
        }

        @Override
        public NameToUuidSnapshot getSnapshot() {
            return new NameToUuidSnapshot(this.uuid, this.name);
        }

        @Getter
        @RequiredArgsConstructor
        public static class NameToUuidSnapshot implements Snapshot {
            private final UUID uuid;
            private final String name;
        }
    }
}
