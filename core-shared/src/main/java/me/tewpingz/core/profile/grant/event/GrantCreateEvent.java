package me.tewpingz.core.profile.grant.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.profile.grant.Grant;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrantCreateEvent implements BridgeEvent {

    private String executorName;
    private UUID playerUuid;
    private Grant grant;

}
