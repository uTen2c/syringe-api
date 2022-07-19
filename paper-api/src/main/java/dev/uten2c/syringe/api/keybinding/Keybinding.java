package dev.uten2c.syringe.api.keybinding;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Keybinding implements IKeybinding {
    private final Key id;
    private final String translateKey;
    private final KeyCode keyCode;

    public Keybinding(@NotNull Key id, @NotNull String translateKey, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = keyCode;
    }

    public Keybinding(@NotNull Key id, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = createTranslateKey(id);
        this.keyCode = keyCode;
    }

    public Keybinding(@NotNull Key id, @NotNull String translateKey) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = KeyCode.ESCAPE;
    }

    public @NotNull Key getId() {
        return id;
    }

    @Override
    public @NotNull String getIdString() {
        return id.asString();
    }

    @Override
    public @NotNull String getTranslateKey() {
        return translateKey;
    }

    @Override
    public @NotNull KeyCode getKeyCode() {
        return keyCode;
    }

    private static @NotNull String createTranslateKey(@NotNull Key key) {
        return String.format("key.%s.%s", key.namespace(), key.value());
    }
}
