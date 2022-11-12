package me.tewpingz.core.server.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.server.Server;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerWhitelistEvent {

    private Server.ServerSnapshot server;

}
