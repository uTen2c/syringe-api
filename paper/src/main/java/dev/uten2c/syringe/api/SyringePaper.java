package dev.uten2c.syringe.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SyringePaper extends JavaPlugin {
    static {
        SyringeApi.setInstance(new SyringeApiImpl());
    }

    @Override
    public void onEnable() {
        SyringeNetworking.setup(this);
        Bukkit.getPluginManager().registerEvents(new PluginEventListener(), this);
    }
}
