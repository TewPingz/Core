package me.tewpingz.core.rank;

import lombok.Data;
import lombok.Getter;
import me.tewpingz.core.Core;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.HashSet;
import java.util.Set;

@Data
public class Rank implements RediGoObject<String, Rank.RankSnapshot>, Comparable<Rank> {

    private final String rankId;

    @RediGoValue(key = "displayName")
    private String displayName = "";

    @RediGoValue(key = "priority")
    private int priority = -1;

    @RediGoValue(key = "color")
    private RankColor color = new RankColor("#FFFFFF", false, false);

    @RediGoValue(key = "prefix")
    private String prefix = "";

    @RediGoValue(key = "suffix")
    private String suffix = "";

    @RediGoValue(key = "permissions")
    private Set<String> permissions = new HashSet<>();

    @RediGoValue(key = "inherits")
    private Set<String> inherits = new HashSet<>();

    public Rank(String rankId) {
        this.rankId = rankId;
    }

    @Override
    public String getKey() {
        return this.rankId;
    }

    @Override
    public RankSnapshot getSnapshot() {
        return new RankSnapshot(this);
    }

    @Override
    public int compareTo(Rank o) {
        return Integer.compare(o.getPriority(), this.priority);
    }

    @Getter
    public static class RankSnapshot implements Snapshot, Comparable<RankSnapshot> {
        private final String rankId, displayName;
        private final int priority;
        private final RankColor color;
        private final String prefix, suffix;
        private final Set<String> permissions, inherits;

        public RankSnapshot(Rank rank) {
            this.rankId = rank.getRankId();
            this.displayName = rank.getDisplayName();
            this.priority = rank.getPriority();
            this.color = rank.getColor();
            this.prefix = rank.getPrefix();
            this.suffix = rank.getSuffix();
            this.permissions = rank.getPermissions();
            this.inherits = rank.getInherits();
        }

        public Set<String> getEffectivePermissions() {
            Set<String> permissions = new HashSet<>();
            this.appendEffectivePermissions(permissions, new HashSet<>());
            return permissions;
        }

        private void appendEffectivePermissions(Set<String> permissions, Set<String> inherits) {
            permissions.addAll(this.permissions);
            this.inherits.forEach(rankId -> {
                // Basically a way to prevent an infinite loop by preventing already inherited roles
                // This is because in theory if somebody was to inherit a rank that inherits the current rank
                // It will create an infinite loop so with this we can create a recursive function that
                // allows us to track the already done inherited ranks and ignore them if they were to come up again
                if (!inherits.contains(rankId)) {
                    RankSnapshot rank = Core.getInstance().getRankManager().getRank(rankId);
                    if (rank != null) {
                        rank.appendEffectivePermissions(permissions, inherits);
                    }
                }
            });
        }

        @Override
        public int compareTo(RankSnapshot o) {
            return Integer.compare(o.getPriority(), this.priority);
        }
    }
}
