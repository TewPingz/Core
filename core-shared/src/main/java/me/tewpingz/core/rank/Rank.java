package me.tewpingz.core.rank;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class Rank implements RediGoObject<String>, Comparable<Rank> {

    private final String rankId;

    @RediGoValue(key = "displayName")
    private String displayName;

    @RediGoValue(key = "priority")
    private int priority = -1;

    @RediGoValue(key = "color")
    private String color = "";

    @RediGoValue(key = "prefix")
    private String prefix = "";

    @RediGoValue(key = "suffix")
    private String suffix = "";

    @RediGoValue(key = "permissions")
    private List<String> permissions = new ArrayList<>();

    @RediGoValue(key = "inherits")
    private List<String> inherits = new ArrayList<>();

    public Rank(String rankId, String displayName) {
        this.rankId = rankId;
        this.displayName = displayName;
    }

    @Override
    public String getKey() {
        return this.rankId;
    }

    @Override
    public int compareTo(Rank o) {
        return Integer.compare(o.getPriority(), this.priority);
    }
}
