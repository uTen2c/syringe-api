package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
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
