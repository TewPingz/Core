package me.tewpingz.core.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerReportEvent implements BridgeEvent {

    private String sender, server, message;
    private UUID target;

}
