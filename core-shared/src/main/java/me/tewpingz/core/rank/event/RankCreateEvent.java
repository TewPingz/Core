package me.tewpingz.core.rank.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tewpingz.core.bridge.BridgeEvent;
import me.tewpingz.core.rank.Rank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankCreateEvent implements BridgeEvent {

    private String executedBy;
    private Rank.RankSnapshot rankSnapshot;

}
