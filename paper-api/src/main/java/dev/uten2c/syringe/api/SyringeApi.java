package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SyringeApi extends AbstractSyringeApi {
    private static @Nullable SyringeApi instance;

    protected SyringeApi() {
    }

    public static @NotNull SyringeApi getInstance() {
        if (instance == null) {
            throw new RuntimeException("Accessed SyringeAPI too early");
        }
        return instance;
    }

    static void setInstance(@NotNull SyringeApi instance) {
        SyringeApi.instance = instance;
    }

    public boolean isSyringeUser(@NotNull Player player) {
        return isSyringeUser(player.getUniqueId());
    }

    public Optional<SyringeVersion> getSyringeVersion(@NotNull Player player) {
        return getSyringeVersion(player.getUniqueId());
    }

    public abstract void registerKeybinding(@NotNull Keybinding keybinding);
}
