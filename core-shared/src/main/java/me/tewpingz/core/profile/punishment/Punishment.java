package me.tewpingz.core.profile.punishment;

import lombok.*;
import me.tewpingz.core.profile.grant.Grant;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class Punishment {

    private final PunishmentType punishmentType;
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

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ExpiredPunishment {

        private final Punishment grant;
        private final String removedBy, removedFor;
        private final long removedAt;

    }
}
