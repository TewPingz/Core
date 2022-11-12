package me.tewpingz.core.rank.grant.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.rank.grant.Grant;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrantRemoveEvent implements BridgeEvent {

    private String executorName;
    private UUID playerUuid;
    private Grant.ExpiredGrant expiredGrant;

}
