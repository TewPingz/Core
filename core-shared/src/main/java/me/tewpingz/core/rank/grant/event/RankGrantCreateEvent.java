package me.tewpingz.core.rank.grant.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.rank.grant.RankGrant;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankGrantCreateEvent implements BridgeEvent {

    private String executorName;
    private UUID playerUuid;
    private RankGrant grant;

}
