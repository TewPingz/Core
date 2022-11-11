package me.tewpingz.core.util.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class AsyncUuid {

    private final String name;

    public UUID fetchUuid() {
        return Core.getInstance().getUuidManager().getUuid(this.name).getUuid();
    }

    public CompletableFuture<UUID> fetchUuidAsync() {
        return CompletableFuture.supplyAsync(this::fetchUuid);
    }
}
