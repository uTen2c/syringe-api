package dev.uten2c.syringe.api.testmod;

import dev.uten2c.syringe.api.SyringeApi;
import dev.uten2c.syringe.api.event.SyringeKeybindingEvents;
import dev.uten2c.syringe.api.keybinding.KeyCode;
import dev.uten2c.syringe.api.keybinding.Keybinding;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public final class KeybindingTest {
    private KeybindingTest() {
    }

    public static void setup() {
        var api = SyringeApi.getInstance();
        api.registerKeybinding(new Keybinding(new Identifier("testmod", "test1"), KeyCode.R));
        api.registerKeybinding(new Keybinding(new Identifier("testmod", "test2"), KeyCode.C));
        api.registerKeybinding(new Keybinding(new Identifier("testmod", "attack"), "key.attack"));

        SyringeKeybindingEvents.PRESSED.register(KeybindingTest::onPressed);
        SyringeKeybindingEvents.RELEASED.register(KeybindingTest::onReleased);
    }

    private static void onPressed(@NotNull ServerPlayerEntity player, @NotNull Identifier identifier) {
        player.sendMessage(Text.of("Pressed: " + identifier));
    }

    private static void onReleased(@NotNull ServerPlayerEntity player, @NotNull Identifier identifier) {
        player.sendMessage(Text.of("Released: " + identifier));
    }
}
