package me.tewpingz.core.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordChatEvent implements BridgeEvent {

    private String serverId, username, message;

}
