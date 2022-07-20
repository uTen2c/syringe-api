package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.perspective.Perspective;
import net.minecraft.server.network.ServerPlayerEntity;
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

    public boolean isSyringeUser(@NotNull ServerPlayerEntity player) {
        return isSyringeUser(player.getUuid());
    }

    public Optional<SyringeVersion> getSyringeVersion(@NotNull ServerPlayerEntity player) {
        return getSyringeVersion(player.getUuid());
    }

    public abstract void registerKeybinding(@NotNull Keybinding keybinding);

    public abstract void displayMessage(@NotNull ServerPlayerEntity player, @NotNull String id, @NotNull MessageContext context);

    public abstract void discardMessage(@NotNull ServerPlayerEntity player, @NotNull String id, long fadeout);

    public void discardMessage(@NotNull ServerPlayerEntity player, @NotNull String id) {
        discardMessage(player, id, 0);
    }

    public abstract void clearMessage(@NotNull ServerPlayerEntity player);

    public abstract void setPerspective(@NotNull ServerPlayerEntity player, @NotNull Perspective perspective);

    public abstract void lockPerspective(@NotNull ServerPlayerEntity player, boolean lock);
}
