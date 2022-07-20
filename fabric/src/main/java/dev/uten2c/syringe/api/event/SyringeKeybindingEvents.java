package dev.uten2c.syringe.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public final class SyringeKeybindingEvents {
    private SyringeKeybindingEvents() {
    }

    public static final Event<Pressed> PRESSED = EventFactory.createArrayBacked(Pressed.class, callbacks -> (player, id) -> {
        for (var callback : callbacks) {
            callback.onPressed(player, id);
        }
    });

    public static final Event<Released> RELEASED = EventFactory.createArrayBacked(Released.class, callbacks -> (player, id) -> {
        for (var callback : callbacks) {
            callback.onReleased(player, id);
        }
    });

    @FunctionalInterface
    public interface Pressed {
        void onPressed(@NotNull ServerPlayerEntity player, @NotNull Identifier id);
    }

    @FunctionalInterface
    public interface Released {
        void onReleased(@NotNull ServerPlayerEntity player, @NotNull Identifier id);
    }
}
