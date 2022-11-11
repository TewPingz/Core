package me.tewpingz.core.rank;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Rank implements RediGoObject<String, Rank.RankSnapshot>, Comparable<Rank> {

    private final String rankId;

    @RediGoValue(key = "displayName")
    private String displayName = "";

    @RediGoValue(key = "priority")
    private int priority = -1;

    @RediGoValue(key = "color")
    private String color = "";

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
        private final String color, prefix, suffix;
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

        @Override
        public int compareTo(RankSnapshot o) {
            return Integer.compare(o.getPriority(), this.priority);
        }
    }
}
