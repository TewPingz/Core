package me.tewpingz.core.bridge;

import me.tewpingz.core.Core;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

public class Bridge {

    private final RTopic topic;

    public Bridge(Core instance) {
        this.topic = instance.getRedissonClient().getTopic("bridge", new BridgeCodec(instance.getGson()));
    }

    public <E extends BridgeEvent> void registerListener(Class<E> clazz, MessageListener<? extends E> listener) {
        this.topic.addListener(clazz, listener);
    }

    public <E extends BridgeEvent> void callEvent(E event) {
        this.topic.publish(event);
    }
}
