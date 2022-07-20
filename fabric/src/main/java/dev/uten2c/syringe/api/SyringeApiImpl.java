package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.perspective.Perspective;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyringeApiImpl extends SyringeApi {
    private static final List<Keybinding> REGISTERED_KEYBINDINGS = new ArrayList<>();

    @Override
    public void registerKeybinding(@NotNull Keybinding keybinding) {
        REGISTERED_KEYBINDINGS.add(keybinding);
    }

    @Override
    public void displayMessage(@NotNull ServerPlayerEntity player, @NotNull String id, @NotNull MessageContext context) {
        var buf = PacketByteBufs.create();
        buf.writeString(id);
        buf.writeText(context.message());
        buf.writeBoolean(context.shadow());
        buf.writeFloat(context.size());
        buf.writeInt(context.lineHeight());
        buf.writeEnumConstant(context.position());
        buf.writeInt(context.offsetX());
        buf.writeInt(context.offsetY());
        buf.writeLong(context.fadein());
        ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_DISPLAY_ID, buf);
    }

    @Override
    public void discardMessage(@NotNull ServerPlayerEntity player, @NotNull String id, long fadeout) {
        var buf = PacketByteBufs.create();
        buf.writeString(id);
        buf.writeLong(fadeout);
        ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_DISCARD_ID, buf);
    }

    @Override
    public void clearMessage(@NotNull ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, SyringeNetworking.MESSAGE_CLEAR_ID, PacketByteBufs.empty());
    }

    @Override
    public void setPerspective(@NotNull ServerPlayerEntity player, @NotNull Perspective perspective) {
        var buf = PacketByteBufs.create();
        buf.writeEnumConstant(perspective);
        ServerPlayNetworking.send(player, SyringeNetworking.PERSPECTIVE_SET_ID, buf);
    }

    @Override
    public void lockPerspective(@NotNull ServerPlayerEntity player, boolean lock) {
        var buf = PacketByteBufs.create();
        buf.writeBoolean(lock);
        ServerPlayNetworking.send(player, SyringeNetworking.PERSPECTIVE_LOCK_ID, buf);
    }

    public static void sendRegisterKeybindingsPacket(@NotNull ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        buf.writeCollection(REGISTERED_KEYBINDINGS, (buf1, keybinding) -> {
            buf1.writeIdentifier(keybinding.getId());
            buf1.writeString(keybinding.getTranslateKey());
            buf1.writeEnumConstant(keybinding.getKeyCode());
        });
        ServerPlayNetworking.send(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf);
    }
}
