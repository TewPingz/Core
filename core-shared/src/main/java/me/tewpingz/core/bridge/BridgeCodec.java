package me.tewpingz.core.bridge;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.RequiredArgsConstructor;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BridgeCodec extends BaseCodec {

    private final Encoder encoder;
    private final Decoder decoder;

    public BridgeCodec(Gson gson) {
        this.encoder = new Encoder(gson);
        this.decoder = new Decoder(gson);
    }

    @Override
    public org.redisson.client.protocol.Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    @Override
    public org.redisson.client.protocol.Encoder getValueEncoder() {
        return this.encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    @RequiredArgsConstructor
    public static class Decoder implements org.redisson.client.protocol.Decoder<Object> {

        private final Gson gson;
        private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
                String string = stream.readUTF();
                String type = stream.readUTF();

                Class<?> clazz = this.getClassFromType(type);

                if (clazz == null) {
                    return null;
                }

                return this.gson.fromJson(string, clazz);
            }
        }

        private Class<?> getClassFromType(String name) {
            Class<?> clazz = this.classMap.get(name);

            if (clazz == null) {
                try {
                    clazz = Class.forName(name);
                    this.classMap.put(name, clazz);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }

            return clazz;
        }
    }

    @RequiredArgsConstructor
    private static class Encoder implements org.redisson.client.protocol.Encoder {

        private final Gson gson;

        @Override
        public ByteBuf encode(Object in) throws IOException {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream os = new ByteBufOutputStream(out);
                os.writeUTF(this.gson.toJson(in));
                os.writeUTF(in.getClass().getName());
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            } catch (Exception e) {
                out.release();
                throw new IOException(e);
            }
        }
    }
}
