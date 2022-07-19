package dev.uten2c.syringe.api.event;

import dev.uten2c.syringe.api.SyringeVersion;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SyringeAsyncPlayerInitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final @NotNull UUID uniqueId;
    private final boolean isSyringeUser;
    private final @Nullable SyringeVersion version;

    public SyringeAsyncPlayerInitEvent(@NotNull UUID uniqueId, boolean isSyringeUser, @Nullable SyringeVersion version) {
        super(true);
        this.uniqueId = uniqueId;
        this.isSyringeUser = isSyringeUser;
        this.version = version;
    }

    public @NotNull UUID getUniqueId() {
        return uniqueId;
    }

    public boolean isSyringeUser() {
        return isSyringeUser;
    }

    public @Nullable SyringeVersion getVersion() {
        return version;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
