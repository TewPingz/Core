package me.tewpingz.core.server.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.server.Server;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ServerShutdownEvent {

    private Server.ServerSnapshot server;

}
