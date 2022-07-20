package dev.uten2c.syringe.api;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public final class FabricEventListener {
    private FabricEventListener() {
    }

    static void setup() {
        ServerPlayConnectionEvents.JOIN.register(FabricEventListener::onJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(FabricEventListener::onDisconnect);
    }

    private static void onJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (SyringeApi.getInstance().isSyringeUser(handler.player)) {
            SyringeApiImpl.sendRegisterKeybindingsPacket(handler.player);
        }
    }

    private static void onDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        var uuid = handler.player.getUuid();
        SyringeApi.getInstance().resetPlayerStates(uuid);
    }
}
