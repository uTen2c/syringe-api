package dev.uten2c.syringe.api.keybinding;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class Keybinding implements IKeybinding {
    private final Identifier id;
    private final String translateKey;
    private final KeyCode keyCode;

    public Keybinding(@NotNull Identifier id, @NotNull String translateKey, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = keyCode;
    }

    public Keybinding(@NotNull Identifier id, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = createTranslateKey(id);
        this.keyCode = keyCode;
    }

    public Keybinding(@NotNull Identifier id, @NotNull String translateKey) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = KeyCode.ESCAPE;
    }

    public @NotNull Identifier getId() {
        return id;
    }

    @Override
    public @NotNull String getIdString() {
        return id.toString();
    }

    @Override
    public @NotNull String getTranslateKey() {
        return translateKey;
    }

    @Override
    public @NotNull KeyCode getKeyCode() {
        return keyCode;
    }

    private static @NotNull String createTranslateKey(@NotNull Identifier key) {
        return String.format("key.%s.%s", key.getNamespace(), key.getPath());
    }
}
