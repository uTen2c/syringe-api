package dev.uten2c.syringe.api.keybinding;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractKeybinding<ID> {
    private final ID id;
    private final String translateKey;
    private final KeyCode keyCode;

    public AbstractKeybinding(@NotNull ID id, @NotNull String translateKey, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = keyCode;
    }

    public AbstractKeybinding(@NotNull ID id, @NotNull KeyCode keyCode) {
        this.id = id;
        this.translateKey = createTranslateKey(id);
        this.keyCode = keyCode;
    }

    public AbstractKeybinding(@NotNull ID id, @NotNull String translateKey) {
        this.id = id;
        this.translateKey = translateKey;
        this.keyCode = KeyCode.ESCAPE;
    }

    public @NotNull ID getId() {
        return id;
    }

    public abstract @NotNull String getIdString();

    public @NotNull String getTranslateKey() {
        return translateKey;
    }

    public @NotNull KeyCode getKeyCode() {
        return keyCode;
    }

    protected abstract @NotNull String createTranslateKey(@NotNull ID id);
}

