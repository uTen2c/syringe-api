package dev.uten2c.syringe.api;

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
import java.util.List;

public final class SyringeApiImpl extends SyringeApi {
    private static final List<Keybinding> REGISTERED_KEYBINDINGS = new ArrayList<>();

    @Override
    public void registerKeybinding(@NotNull Keybinding keybinding) {
        REGISTERED_KEYBINDINGS.add(keybinding);
    }

    @Override
    public void displayMessage(@NotNull Player player, @NotNull String id, @NotNull MessageContext context) {
        var buf = Bufs.create();
        buf.writeUtf(id);
        buf.writeComponent(context.message());
        buf.writeBoolean(context.shadow());
        buf.writeFloat(context.size());
        buf.writeInt(context.lineHeight());
        buf.writeEnum(context.position());
        buf.writeInt(context.offsetX());
        buf.writeInt(context.offsetY());
        buf.writeLong(context.fadein());
        sendPacket(player, SyringeNetworking.MESSAGE_DISPLAY_ID, buf);
    }

    @Override
    public void discardMessage(@NotNull Player player, @NotNull String id, long fadeout) {
        var buf = Bufs.create();
        buf.writeUtf(id);
        buf.writeLong(fadeout);
        sendPacket(player, SyringeNetworking.MESSAGE_DISCARD_ID, buf);
    }

    @Override
    public void clearMessage(@NotNull Player player) {
        sendPacket(player, SyringeNetworking.MESSAGE_CLEAR_ID, Bufs.empty());
    }

    @Override
    public void setPerspective(@NotNull Player player, @NotNull Perspective perspective) {
        var buf = Bufs.create();
        buf.writeEnum(perspective);
        sendPacket(player, SyringeNetworking.PERSPECTIVE_SET_ID, buf);
    }

    @Override
    public void lockPerspective(@NotNull Player player, boolean lock) {
        var buf = Bufs.create();
        buf.writeBoolean(lock);
        sendPacket(player, SyringeNetworking.PERSPECTIVE_LOCK_ID, buf);
    }

    public static void sendRegisterKeybindingsPacket(@NotNull Player player) {
        var buf = Bufs.create();
        buf.writeCollection(REGISTERED_KEYBINDINGS, (buf1, keybinding) -> {
            buf1.writeUtf(keybinding.getIdString());
            buf1.writeUtf(keybinding.getTranslateKey());
            buf1.writeEnum(keybinding.getKeyCode());
        });
        sendPacket(player, SyringeNetworking.KEYBINDING_REGISTER_ID, buf);
    }

    private static void sendPacket(@NotNull Player player, @NotNull Key id, @NotNull FriendlyByteBuf buf) {
        var packetId = new ResourceLocation(id.asString());
        var packet = new ClientboundCustomPayloadPacket(packetId, buf);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
