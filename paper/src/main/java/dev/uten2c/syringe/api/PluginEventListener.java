package dev.uten2c.syringe.api;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PluginEventListener implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        if (SyringeApi.getInstance().isSyringeUser(player)) {
            SyringeApiImpl.sendRegisterKeybindingsPacket(player);
        }
    }

    @EventHandler
    private void onConnectionClose(PlayerConnectionCloseEvent e) {
        SyringeApi.getInstance().resetPlayerStates(e.getPlayerUniqueId());
    }
}
