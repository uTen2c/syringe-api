package dev.uten2c.syringe.api.keybinding;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Keybinding extends AbstractKeybinding<Key> {
    public Keybinding(@NotNull Key key, @NotNull String translateKey, @NotNull KeyCode keyCode) {
        super(key, translateKey, keyCode);
    }

    public Keybinding(@NotNull Key key, @NotNull KeyCode keyCode) {
        super(key, keyCode);
    }

    public Keybinding(@NotNull Key key, @NotNull String translateKey) {
        super(key, translateKey);
    }

    @Override
    public @NotNull String getIdString() {
        return getId().asString();
    }

    @Override
    protected @NotNull String createTranslateKey(@NotNull Key key) {
        return String.format("key.%s.%s", key.namespace(), key.value());
    }
}
