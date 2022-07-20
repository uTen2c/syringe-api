package dev.uten2c.syringe.api;

import net.fabricmc.api.ModInitializer;

public class SyringeFabric implements ModInitializer {
    static {
        SyringeApi.setInstance(new SyringeApiImpl());
    }

    @Override
    public void onInitialize() {
        FabricEventListener.setup();
        SyringeNetworking.setup();
    }
}
