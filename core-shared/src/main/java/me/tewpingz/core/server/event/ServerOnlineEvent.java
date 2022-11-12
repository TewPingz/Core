package me.tewpingz.core.server.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.server.Server;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ServerOnlineEvent implements BridgeEvent {

    private Server.ServerSnapshot server;

}
