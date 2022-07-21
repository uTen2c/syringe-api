package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.hud.HudPart;
import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.perspective.Perspective;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SyringeApiImpl extends SyringeApi {
    private static final List<Keybinding> REGISTERED_KEYBINDINGS = new ArrayList<>();

    @Override
    public void registerKeybinding(@NotNull Keybinding keybinding) {
        REGISTERED_KEYBINDINGS.add(keybinding);
    }

    @Override
    public void displayMessage(@NotNull ServerPlayerEntity player, @NotNull String id, @NotNull MessageContext context) {
        sendPacket(player, SyringeNetworking.MESSAGE_DISPLAY_ID, buf -> {
            buf.writeString(id);
            buf.writeText(context.message());
            buf.writeBoolean(context.shadow());
            buf.writeFloat(context.size());
            buf.writeInt(context.lineHeight());
            buf.writeEnumConstant(context.position());
            buf.writeInt(context.offsetX());
            buf.writeInt(context.offsetY());
            buf.writeLong(context.fadein());
        });
    }

    @Override
    public void discardMessage(@NotNull ServerPlayerEntity player, @NotNull String id, long fadeout) {
        sendPacket(player, SyringeNetworking.MESSAGE_DISCARD_ID, buf -> {
            buf.writeString(id);
            buf.writeLong(fadeout);
        });
    }

    @Override
    public void clearMessage(@NotNull ServerPlayerEntity player) {
        sendPacketWithEmptyBuf(player, SyringeNetworking.MESSAGE_CLEAR_ID);
    }

    @Override
    public void setPerspective(@NotNull ServerPlayerEntity player, @NotNull Perspective perspective) {
        sendPacket(player, SyringeNetworking.PERSPECTIVE_SET_ID, buf -> {
            buf.writeEnumConstant(perspective);
        });
    }

    @Override
    public void lockPerspective(@NotNull ServerPlayerEntity player, boolean lock) {
        sendPacket(player, SyringeNetworking.PERSPECTIVE_LOCK_ID, buf -> {
            buf.writeBoolean(lock);
        });
    }

    @Override
    public void hideHudParts(@NotNull ServerPlayerEntity player, @NotNull Collection<HudPart> hudParts) {
        sendPacket(player, SyringeNetworking.HUD_HIDE_ID, buf -> {
            buf.writeCollection(hudParts, PacketByteBuf::writeEnumConstant);
        });
    }

    @Override
    public void showHudParts(@NotNull ServerPlayerEntity player, @NotNull Collection<HudPart> hudParts) {
        sendPacket(player, SyringeNetworking.HUD_SHOW_ID, buf -> {
            buf.writeCollection(hudParts, PacketByteBuf::writeEnumConstant);
        });
    }

    public static void sendRegisterKeybindingsPacket(@NotNull ServerPlayerEntity player) {
        sendPacket(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf -> {
            buf.writeCollection(REGISTERED_KEYBINDINGS, (buf1, keybinding) -> {
                buf1.writeIdentifier(keybinding.getId());
                buf1.writeString(keybinding.getTranslateKey());
                buf1.writeEnumConstant(keybinding.getKeyCode());
            });
        });
    }

    private static void sendPacket(@NotNull ServerPlayerEntity player, @NotNull Identifier id, @NotNull Consumer<PacketByteBuf> consumer) {
        if (!getInstance().isSyringeUser(player)) {
            return;
        }
        var buf = PacketByteBufs.create();
        consumer.accept(buf);
        ServerPlayNetworking.send(player, id, buf);
    }

    private static void sendPacketWithEmptyBuf(@NotNull ServerPlayerEntity player, @NotNull Identifier id) {
        if (!getInstance().isSyringeUser(player)) {
            return;
        }
        ServerPlayNetworking.send(player, id, PacketByteBufs.empty());
    }
}
