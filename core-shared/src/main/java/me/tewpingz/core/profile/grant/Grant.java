package me.tewpingz.core.profile.grant;

import lombok.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;
import net.kyori.adventure.text.Component;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class Grant {

    private final String rankId;
    private final String executor;
    private final String reason;
    private final long startTimestamp;
    private final long duration;

    public boolean isInfinite() {
        return this.duration <= -1;
    }

    public long getExpireTimestamp() {
        return this.isInfinite() ? Long.MAX_VALUE : this.startTimestamp + this.duration;
    }

    public long getTimeLeft() {
        return this.isInfinite() ? Long.MAX_VALUE : this.getExpireTimestamp() - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return this.getTimeLeft() <= 0;
    }

    public Component getRankNameComponent() {
        Rank.RankSnapshot rankSnapshot = this.getRankSnapshot();
        if (rankSnapshot != null) {
            return rankSnapshot.getDisplayNameWithColor();
        } else {
            return Component.text(this.rankId);
        }
    }

    public Rank.RankSnapshot getRankSnapshot() {
        return Core.getInstance().getRankManager().getCachedRank(this.rankId);
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ExpiredGrant {

        private final Grant grant;
        private final String removedBy, removedFor;
        private final long removedAt;

    }
}
