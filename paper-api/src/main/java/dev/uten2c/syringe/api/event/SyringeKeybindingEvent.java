package dev.uten2c.syringe.api.event;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SyringeKeybindingEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final @NotNull Key id;

    public SyringeKeybindingEvent(@NotNull Player who, @NotNull Key id) {
        super(who);
        this.id = id;
    }

    public @NotNull Key getId() {
        return id;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
