package me.tewpingz.core.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import net.dv8tion.jda.api.JDA;

public class RankBridgeListener {

    public RankBridgeListener(JDA jda) {
        Core.getInstance().getBridge().registerListener(RankCreateEvent.class, (charSequence, event) -> {

        });


        Core.getInstance().getBridge().registerListener(RankUpdateEvent.class, (charSequence, event) -> {

        });
    }

}
