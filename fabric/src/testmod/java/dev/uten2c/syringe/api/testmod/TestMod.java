package dev.uten2c.syringe.api.testmod;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        HandshakeTest.setup();
        KeybindingTest.setup();
    }
}
