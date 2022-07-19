package dev.uten2c.syringe.api.event;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SyringeKeyReleasedEvent extends SyringeKeybindingEvent {
    public SyringeKeyReleasedEvent(@NotNull Player who, @NotNull Key id) {
        super(who, id);
    }
}
