package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.hud.HudPart;
import dev.uten2c.syringe.api.keybinding.Keybinding;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.perspective.Perspective;
import dev.uten2c.syringe.api.util.Bufs;
import net.kyori.adventure.key.Key;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class SyringeApiImpl extends SyringeApi {
    private static final List<Keybinding> REGISTERED_KEYBINDINGS = new ArrayList<>();

    @Override
    public void registerKeybinding(@NotNull Keybinding keybinding) {
        REGISTERED_KEYBINDINGS.add(keybinding);
    }

    @Override
    public void displayMessage(@NotNull Player player, @NotNull String id, @NotNull MessageContext context) {
        sendPacket(player, SyringeNetworking.MESSAGE_DISPLAY_ID, buf -> {
            buf.writeUtf(id);
            buf.writeComponent(context.message());
            buf.writeBoolean(context.shadow());
            buf.writeFloat(context.size());
            buf.writeInt(context.lineHeight());
            buf.writeEnum(context.position());
            buf.writeInt(context.offsetX());
            buf.writeInt(context.offsetY());
            buf.writeLong(context.fadein());
        });
    }

    @Override
    public void discardMessage(@NotNull Player player, @NotNull String id, long fadeout) {
        sendPacket(player, SyringeNetworking.MESSAGE_DISCARD_ID, buf -> {
            buf.writeUtf(id);
            buf.writeLong(fadeout);
        });
    }

    @Override
    public void clearMessage(@NotNull Player player) {
        sendPacketWithEmptyBuf(player, SyringeNetworking.MESSAGE_CLEAR_ID);
    }

    @Override
    public void setPerspective(@NotNull Player player, @NotNull Perspective perspective) {
        sendPacket(player, SyringeNetworking.PERSPECTIVE_SET_ID, buf -> {
            buf.writeEnum(perspective);
        });
    }

    @Override
    public void lockPerspective(@NotNull Player player, boolean lock) {
        sendPacket(player, SyringeNetworking.PERSPECTIVE_LOCK_ID, buf -> {
            buf.writeBoolean(lock);
        });
    }

    @Override
    public void hideHudParts(@NotNull Player player, @NotNull Collection<HudPart> hudParts) {
        sendPacket(player, SyringeNetworking.HUD_HIDE_ID, buf -> {
            buf.writeCollection(hudParts, FriendlyByteBuf::writeEnum);
        });
    }

    @Override
    public void showHudParts(@NotNull Player player, @NotNull Collection<HudPart> hudParts) {
        sendPacket(player, SyringeNetworking.HUD_SHOW_ID, buf -> {
            buf.writeCollection(hudParts, FriendlyByteBuf::writeEnum);
        });
    }

    @Override
    public void setDirection(@NotNull Player player, boolean relative, float yaw, float pitch) {
        sendPacket(player, SyringeNetworking.CAMERA_SET_DIRECTION_ID, buf -> {
            buf.writeBoolean(relative);
            buf.writeFloat(yaw);
            buf.writeFloat(pitch);
        });
    }

    @Override
    public void zoom(@NotNull Player player, float multiplier) {
        sendPacket(player, SyringeNetworking.CAMERA_ZOOM_ID, buf -> {
            buf.writeFloat(multiplier);
        });
    }

    @Override
    public void lockCamera(@NotNull Player player, boolean lock) {
        sendPacket(player, SyringeNetworking.CAMERA_LOCK_ID, buf -> {
            buf.writeBoolean(lock);
        });
    }

    @Override
    public void lockMovement(@NotNull Player player, boolean lock) {
        sendPacket(player, SyringeNetworking.MOVEMENT_LOCK_ID, buf -> {
            buf.writeBoolean(lock);
        });
    }

    public static void sendRegisterKeybindingsPacket(@NotNull Player player) {
        sendPacket(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf -> {
            buf.writeCollection(REGISTERED_KEYBINDINGS, (buf1, keybinding) -> {
                buf1.writeUtf(keybinding.getIdString());
                buf1.writeUtf(keybinding.getTranslateKey());
                buf1.writeEnum(keybinding.getKeyCode());
            });
        });
    }

    private static void sendPacket(@NotNull Player player, @NotNull Key id, @NotNull Consumer<FriendlyByteBuf> consumer) {
        if (!getInstance().isSyringeUser(player)) {
            return;
        }
        var buf = Bufs.create();
        consumer.accept(buf);
        forceSendPacket(player, id, buf);
    }

    private static void sendPacketWithEmptyBuf(@NotNull Player player, @NotNull Key id) {
        if (!getInstance().isSyringeUser(player)) {
            return;
        }
        forceSendPacket(player, id, Bufs.empty());
    }

    private static void forceSendPacket(@NotNull Player player, @NotNull Key id, @NotNull FriendlyByteBuf buf) {
        var packetId = new ResourceLocation(id.asString());
        var packet = new ClientboundCustomPayloadPacket(packetId, buf);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
