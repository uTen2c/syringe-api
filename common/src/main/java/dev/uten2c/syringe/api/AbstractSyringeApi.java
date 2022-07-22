package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.hud.HudPart;
import dev.uten2c.syringe.api.keybinding.AbstractKeybinding;
import dev.uten2c.syringe.api.perspective.Perspective;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractSyringeApi<PLAYER, KEYBINDING extends AbstractKeybinding<?>, MESSAGE_CONTEXT> {
    public static final int DEFAULT_MESSAGE_LINE_HEIGHT = 9;
    static final ConcurrentMap<UUID, SyringeVersion> PLAYER_VERSION_MAP = new ConcurrentHashMap<>();

    public boolean isSyringeUser(@NotNull UUID uniqueId) {
        return PLAYER_VERSION_MAP.containsKey(uniqueId);
    }

    public abstract boolean isSyringeUser(@NotNull PLAYER player);

    public Optional<SyringeVersion> getSyringeVersion(@NotNull UUID uniqueId) {
        return Optional.ofNullable(PLAYER_VERSION_MAP.get(uniqueId));
    }

    public abstract Optional<SyringeVersion> getSyringeVersion(@NotNull PLAYER player);

    public void resetPlayerStates(@NotNull UUID uniqueId) {
        PLAYER_VERSION_MAP.remove(uniqueId);
    }

    public abstract void registerKeybinding(@NotNull KEYBINDING keybinding);

    public abstract void displayMessage(@NotNull PLAYER player, @NotNull String id, @NotNull MESSAGE_CONTEXT context);

    public abstract void discardMessage(@NotNull PLAYER player, @NotNull String id, long fadeout);

    public void discardMessage(@NotNull PLAYER player, @NotNull String id) {
        discardMessage(player, id, 0);
    }

    public abstract void clearMessage(@NotNull PLAYER player);

    public abstract void setPerspective(@NotNull PLAYER player, @NotNull Perspective perspective);

    public abstract void lockPerspective(@NotNull PLAYER player, boolean lock);


    public abstract void hideHudParts(@NotNull PLAYER player, @NotNull Collection<HudPart> hudParts);

    public void hideHudParts(@NotNull PLAYER player, @NotNull HudPart... hudParts) {
        hideHudParts(player, Arrays.asList(hudParts));
    }

    public abstract void showHudParts(@NotNull PLAYER player, @NotNull Collection<HudPart> hudParts);

    public void showHudParts(@NotNull PLAYER player, @NotNull HudPart... hudParts) {
        showHudParts(player, Arrays.asList(hudParts));
    }

    public abstract void setDirection(@NotNull PLAYER player, boolean relative, float yaw, float pitch);

    public abstract void zoom(@NotNull PLAYER player, float multiplier);

    public abstract void lockCamera(@NotNull PLAYER player, boolean lock);

    public abstract void lockMovement(@NotNull PLAYER player, boolean lock);
}
