package dev.uten2c.syringe.api;

import dev.uten2c.syringe.api.command.SyringeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class SyringeFabric implements ModInitializer, PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        SyringeApi.setInstance(new SyringeApiImpl());
    }

    @Override
    public void onInitialize() {
        FabricEventListener.setup();
        SyringeNetworking.setup();
        CommandRegistrationCallback.EVENT.register(SyringeCommand::register);
    }
}
