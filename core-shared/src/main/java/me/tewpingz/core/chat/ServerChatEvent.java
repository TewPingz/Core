package me.tewpingz.core.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerChatEvent implements BridgeEvent {

    private UUID playerId;
    private String serverId, username, message;

}
