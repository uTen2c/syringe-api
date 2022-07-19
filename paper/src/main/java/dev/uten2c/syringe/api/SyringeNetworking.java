package dev.uten2c.syringe.api;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import dev.uten2c.syringe.api.event.SyringeAsyncPlayerInitEvent;
import dev.uten2c.syringe.api.event.SyringeKeyPressedEvent;
import dev.uten2c.syringe.api.event.SyringeKeyReleasedEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.kyori.adventure.key.Key;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class SyringeNetworking implements Listener, PluginMessageListener {
    private static final String NAMESPACE = "syringe";
    private static final String PACKET_HANDLER_CHANNEL = "syringe_packet_handler";
    private static final Map<Player, Set<Key>> PRESSING_KEYS = new HashMap<>();
    static final Key HANDSHAKE_ID = new NamespacedKey(NAMESPACE, "handshake");
    static final Key MESSAGE_DISPLAY_ID = new NamespacedKey(NAMESPACE, "message/display");
    static final Key MESSAGE_DISCARD_ID = new NamespacedKey(NAMESPACE, "message/discard");
    static final Key MESSAGE_CLEAR_ID = new NamespacedKey(NAMESPACE, "message/clear");
    static final Key KEYBINDING_REGISTER_ID = new NamespacedKey(NAMESPACE, "keybinding/register");
    static final Key PERSPECTIVE_SET_ID = new NamespacedKey(NAMESPACE, "perspective/set");
    static final Key PERSPECTIVE_LOCK_ID = new NamespacedKey(NAMESPACE, "perspective/lock");
    static final Key CAMERA_SET_DIRECTION_ID = new NamespacedKey(NAMESPACE, "camera/set_direction");
    static final Key CAMERA_ZOOM_ID = new NamespacedKey(NAMESPACE, "camera/zoom");
    static final Key HUD_HIDE_ID = new NamespacedKey(NAMESPACE, "hud/hide");
    static final Key HUD_SHOW_ID = new NamespacedKey(NAMESPACE, "hud/show");
    static final Key KEYBINDING_PRESSED_ID = new NamespacedKey(NAMESPACE, "keybinding/pressed");
    static final Key KEYBINDING_RELEASED_ID = new NamespacedKey(NAMESPACE, "keybinding/released");

    static void setup(Plugin plugin) {
        var listener = new SyringeNetworking();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        registerListener(plugin, KEYBINDING_PRESSED_ID, listener);
        registerListener(plugin, KEYBINDING_RELEASED_ID, listener);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        var buf = new FriendlyByteBuf(Unpooled.copiedBuffer(message));
        if (channel.equals(KEYBINDING_PRESSED_ID.asString())) {
            onPressed(player, buf);
        } else if (channel.equals(KEYBINDING_RELEASED_ID.asString())) {
            onReleased(player, buf);
        }
    }

    @EventHandler
    private void onPreLogin(AsyncPlayerPreLoginEvent e) {
        var networkIo = ((CraftServer) Bukkit.getServer()).getServer().getConnection();
        assert networkIo != null;
        networkIo.getConnections().stream()
            .filter(conn -> !conn.channel.pipeline().names().contains(PACKET_HANDLER_CHANNEL))
            .forEach(conn -> {
                var queryId = ThreadLocalRandom.current().nextInt();
                var handler = new HandshakeHandler(e.getUniqueId(), queryId);
                conn.channel.pipeline().addBefore("packet_handler", PACKET_HANDLER_CHANNEL, handler);
                conn.send(createHandshakePacket(queryId));
            });
    }

    @EventHandler
    private void onConnectionClose(PlayerConnectionCloseEvent e) {
        SyringeApiImpl.PLAYER_VERSION_MAP.remove(e.getPlayerUniqueId());
    }

    private static void onPressed(@NotNull Player player, @NotNull FriendlyByteBuf buf) {
        var key = readKey(buf);
        var pressingKeys = PRESSING_KEYS.getOrDefault(player, new HashSet<>());
        pressingKeys.add(key);
        PRESSING_KEYS.put(player, pressingKeys);
        new SyringeKeyPressedEvent(player, key).callEvent();
    }

    private static void onReleased(@NotNull Player player, @NotNull FriendlyByteBuf buf) {
        var key = readKey(buf);
        var pressingKeys = PRESSING_KEYS.getOrDefault(player, new HashSet<>());
        if (pressingKeys.contains(key)) {
            pressingKeys.remove(key);
            PRESSING_KEYS.put(player, pressingKeys);
            new SyringeKeyReleasedEvent(player, key).callEvent();
        }
    }

    private static void registerListener(Plugin plugin, Key channel, PluginMessageListener listener) {
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel.asString(), listener);
    }

    private static ClientboundCustomQueryPacket createHandshakePacket(int queryId) {
        var buf = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);
        var handshakeId = new ResourceLocation(HANDSHAKE_ID.namespace(), HANDSHAKE_ID.value());
        return new ClientboundCustomQueryPacket(queryId, handshakeId, buf);
    }

    @SuppressWarnings("PatternValidation")
    private static Key readKey(FriendlyByteBuf buf) {
        return Key.key(buf.readUtf());
    }

    private static class HandshakeHandler extends ChannelDuplexHandler {
        private final UUID uuid;
        private final int queryId;

        public HandshakeHandler(UUID uuid, int queryId) {
            this.uuid = uuid;
            this.queryId = queryId;
        }

        @Override
        public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
            if (msg instanceof ServerboundCustomQueryPacket packet) {
                if (packet.getTransactionId() != queryId) {
                    return;
                }
                var data = packet.getData();
                SyringeVersion version = null;
                if (data != null) {
                    version = new SyringeVersion(data.readUtf(), data.readInt());
                    SyringeApiImpl.PLAYER_VERSION_MAP.put(uuid, version);
                }
                new SyringeAsyncPlayerInitEvent(uuid, data != null, version).callEvent();
                return;
            }
            super.channelRead(ctx, msg);
        }
    }
}
