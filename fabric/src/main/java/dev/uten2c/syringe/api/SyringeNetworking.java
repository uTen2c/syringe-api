package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.event.SyringeAsyncPlayerInitCallback;
import dev.uten2c.syringe.api.event.SyringeKeybindingEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class SyringeNetworking {
    public static final String NAMESPACE = "syringe";

    // Login
    static final Identifier HANDSHAKE_ID = new Identifier(NAMESPACE, "handshake");

    // S2C
    static final Identifier MESSAGE_DISPLAY_ID = new Identifier(NAMESPACE, "message/display");
    static final Identifier MESSAGE_DISCARD_ID = new Identifier(NAMESPACE, "message/discard");
    static final Identifier MESSAGE_CLEAR_ID = new Identifier(NAMESPACE, "message/clear");
    static final Identifier KEYBINDING_REGISTER_ID = new Identifier(NAMESPACE, "keybinding/register");
    static final Identifier PERSPECTIVE_SET_ID = new Identifier(NAMESPACE, "perspective/set");
    static final Identifier PERSPECTIVE_LOCK_ID = new Identifier(NAMESPACE, "perspective/lock");
    static final Identifier CAMERA_SET_DIRECTION_ID = new Identifier(NAMESPACE, "camera/set_direction");
    static final Identifier CAMERA_ZOOM_ID = new Identifier(NAMESPACE, "camera/zoom");
    static final Identifier HUD_HIDE_ID = new Identifier(NAMESPACE, "hud/hide");
    static final Identifier HUD_SHOW_ID = new Identifier(NAMESPACE, "hud/show");

    // C2S
    static final Identifier KEYBINDING_PRESSED_ID = new Identifier(NAMESPACE, "keybinding/pressed");
    static final Identifier KEYBINDING_RELEASED_ID = new Identifier(NAMESPACE, "keybinding/released");

    private SyringeNetworking() {
    }

    static void setup() {
        ServerLoginConnectionEvents.QUERY_START.register(SyringeNetworking::onQueryStart);
        ServerLoginNetworking.registerGlobalReceiver(SyringeNetworking.HANDSHAKE_ID, SyringeNetworking::handshake);
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDING_PRESSED_ID, SyringeNetworking::onKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(KEYBINDING_RELEASED_ID, SyringeNetworking::onKeyReleased);
    }

    private static void onQueryStart(ServerLoginNetworkHandler networkHandler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
        sender.sendPacket(HANDSHAKE_ID, PacketByteBufs.empty());
    }

    private static void handshake(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
        SyringeVersion version = null;
        var profile = handler.profile;
        assert profile != null;
        if (understood) {
            version = new SyringeVersion(buf.readString(), buf.readInt());
            SyringeApiImpl.PLAYER_VERSION_MAP.put(profile.getId(), version);
        }
        SyringeAsyncPlayerInitCallback.EVENT.invoker().init(profile, version != null, version, handler);
    }

    private static void onKeyPressed(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var id = buf.readIdentifier();
        SyringeKeybindingEvents.PRESSED.invoker().onPressed(player, id);
    }

    private static void onKeyReleased(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        var id = buf.readIdentifier();
        SyringeKeybindingEvents.RELEASED.invoker().onReleased(player, id);
    }
}
