package me.tewpingz.core.profile.punishment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.profile.punishment.Punishment;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentRemoveEvent implements BridgeEvent {

    private String executorName;
    private UUID playerUuid;
    private Punishment.ExpiredPunishment expiredPunishment;

}
