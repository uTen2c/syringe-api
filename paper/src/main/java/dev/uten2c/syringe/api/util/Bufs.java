package dev.uten2c.syringe.api.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public final class Bufs {
    private static final FriendlyByteBuf EMPTY = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);

    private Bufs() {
    }

    public static FriendlyByteBuf create() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf empty() {
        return EMPTY;
    }
}
