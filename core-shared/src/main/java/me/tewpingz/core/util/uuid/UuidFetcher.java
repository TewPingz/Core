package me.tewpingz.core.util.uuid;

import com.google.gson.JsonObject;
import me.tewpingz.core.Core;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class UuidFetcher {
    private static final String ID_TO_NAME_ENDPOINT = "https://api.mojang.com/user/profile/%s";
    private static final String NAME_TO_ID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft/%s";

    public String fetchName(UUID playerID) {
        JsonObject object = this.requestREST(URI.create(ID_TO_NAME_ENDPOINT.formatted(playerID)));
        return object == null ? null : object.get("name").getAsString();
    }

    public UUID fetchUuid(String playerName) {
        JsonObject object = this.requestREST(URI.create(NAME_TO_ID_ENDPOINT.formatted(playerName)));
        return object == null ? null : this.fromDashLes(object.get("id").getAsString());
    }

    private JsonObject requestREST(URI uri) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response == null || response.statusCode() != 200) {
            return null;
        }
        return Core.getInstance().getGson().fromJson(response.body(), JsonObject.class);
    }

    private UUID fromDashLes(String dashLesUuid) {
        return UUID.fromString(dashLesUuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}