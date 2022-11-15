package me.tewpingz.core.queue;

import lombok.*;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.redigo.data.RediGoObject;
import me.tewpingz.redigo.data.RediGoValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Queue implements RediGoObject<String, Queue.QueueSnapshot> {

    @Getter
    private final String serverId;

    @RediGoValue(key = "queuedPlayers")
    private List<QueuePlayer> queuedPlayers = new ArrayList<>();

    public void addPlayer(Profile.ProfileSnapshot profile) {
        int priority = profile.getDisplayRank().getPriority();
        QueuePlayer queuePlayer = new QueuePlayer(profile.getPlayerId(), priority, System.currentTimeMillis());
        this.queuedPlayers.add(queuePlayer);
        this.queuedPlayers.sort(QueuePlayer::compareTo);
    }

    public void removePlayer(UUID uuid) {
        this.queuedPlayers.removeIf(queuePlayer -> queuePlayer.getUuid().equals(uuid));
    }

    public QueuePlayer getFirstEntry() {
        if (this.queuedPlayers.isEmpty()) {
            return null;
        }
        return this.queuedPlayers.get(0);
    }

    public void removeEntry(QueuePlayer player) {
        this.queuedPlayers.remove(player);
    }

    @Override
    public String getKey() {
        return this.serverId;
    }

    @Override
    public QueueSnapshot getSnapshot() {
        return new QueueSnapshot(this.serverId, List.copyOf(this.queuedPlayers));
    }

    @Getter
    @AllArgsConstructor
    public static class QueueSnapshot implements Snapshot {
        private final String serverId;
        private final List<QueuePlayer> queuePlayers;

        public boolean hasPlayer(UUID uuid) {
            return this.getPlayer(uuid).isPresent();
        }

        public Optional<QueuePlayer> getPlayer(UUID uuid) {
            return this.queuePlayers.stream().filter(queuePlayer -> queuePlayer.getUuid().equals(uuid)).findFirst();
        }

        public int getPosition(UUID uuid) {
            Optional<QueuePlayer> optional = this.getPlayer(uuid);

            if (optional.isEmpty()) {
                return -1;
            }

            return this.queuePlayers.indexOf(optional.get());
        }

    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class QueuePlayer implements Comparable<QueuePlayer> {
        private final UUID uuid;
        private final int priority;
        private final long joinTime;

        @Override
        public int compareTo(@NotNull Queue.QueuePlayer o) {
            int comparedPriority = Integer.compare(-this.priority, -o.getPriority());

            if (comparedPriority == 0) {
                return Long.compare(this.joinTime, o.getJoinTime());
            }

            return comparedPriority;
        }
    }
}
