package dev.uten2c.syringe.api.testmod;

import dev.uten2c.syringe.api.event.SyringeAsyncPlayerInitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HandshakeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeTest.class);

    private HandshakeTest() {
    }

    public static void setup() {
        SyringeAsyncPlayerInitCallback.EVENT.register((uniqueId, isSyringeUser, version, connection) -> {
            LOGGER.info("SyringeAsyncPlayerInitCallback was called");
            if (version != null) {
                LOGGER.info(String.format("%s is Syringe user (version: %s, protocol: %d)", uniqueId, version.version(), version.protocol()));
            } else {
                LOGGER.info(String.format("%s is not Syringe user", uniqueId));
            }
        });
    }
}
