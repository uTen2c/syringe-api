package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.hud.HudPart;
import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.perspective.Perspective;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
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

    public abstract void displayMessage(@NotNull Player player, @NotNull String id, @NotNull MessageContext context);

    public abstract void discardMessage(@NotNull Player player, @NotNull String id, long fadeout);

    public void discardMessage(@NotNull Player player, @NotNull String id) {
        discardMessage(player, id, 0);
    }

    public abstract void clearMessage(@NotNull Player player);

    public abstract void setPerspective(@NotNull Player player, @NotNull Perspective perspective);

    public abstract void lockPerspective(@NotNull Player player, boolean lock);

    public abstract void hideHudParts(@NotNull Player player, @NotNull Collection<HudPart> hudParts);

    public void hideHudParts(@NotNull Player player, @NotNull HudPart... hudParts) {
        hideHudParts(player, Arrays.asList(hudParts));
    }

    public abstract void showHudParts(@NotNull Player player, @NotNull Collection<HudPart> hudParts);

    public void showHudParts(@NotNull Player player, @NotNull HudPart... hudParts) {
        showHudParts(player, Arrays.asList(hudParts));
    }

    public abstract void setDirection(@NotNull Player player, boolean relative, float yaw, float pitch);

    public abstract void zoom(@NotNull Player player, double multiplier);
}
