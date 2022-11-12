package me.tewpingz.core.server.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.server.Server;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerShutdownEvent implements BridgeEvent {

    private Server.ServerSnapshot server;

}
