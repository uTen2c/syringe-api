package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SyringeApi extends AbstractSyringeApi<ServerPlayerEntity, Keybinding, MessageContext> {
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
}
