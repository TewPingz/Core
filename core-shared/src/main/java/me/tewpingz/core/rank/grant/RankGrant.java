package me.tewpingz.core.rank.grant;

import lombok.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class RankGrant {

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

    public Rank.RankSnapshot getRankSnapshot() {
        return Core.getInstance().getRankManager().getRank(this.rankId);
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ExpiredRankGrant {

        private final RankGrant rankGrant;
        private final String removedBy, removedFor;
        private final long removedAt;

    }
}
