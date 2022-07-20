package dev.uten2c.syringe.api.event;

import com.mojang.authlib.GameProfile;
import dev.uten2c.syringe.api.SyringeVersion;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SyringeAsyncPlayerInitCallback {
    Event<SyringeAsyncPlayerInitCallback> EVENT = EventFactory.createArrayBacked(SyringeAsyncPlayerInitCallback.class, callbacks -> (profile, isSyringeUser, version, handler) -> {
        for (var callback : callbacks) {
            callback.init(profile, isSyringeUser, version, handler);
        }
    });

    void init(@NotNull GameProfile profile, boolean isSyringeUser, @Nullable SyringeVersion version, @NotNull ServerLoginNetworkHandler handler);
}
