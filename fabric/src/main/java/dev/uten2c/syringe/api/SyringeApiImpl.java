package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.keybinding.Keybinding;
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
