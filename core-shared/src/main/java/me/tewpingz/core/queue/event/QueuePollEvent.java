package me.tewpingz.core.queue.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePollEvent implements BridgeEvent {

    private String serverId;

}
