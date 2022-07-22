package dev.uten2c.syringe.api.keybinding;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class Keybinding extends AbstractKeybinding<Identifier> {
    public Keybinding(@NotNull Identifier identifier, @NotNull String translateKey, @NotNull KeyCode keyCode) {
        super(identifier, translateKey, keyCode);
    }

    public Keybinding(@NotNull Identifier identifier, @NotNull KeyCode keyCode) {
        super(identifier, keyCode);
    }

    public Keybinding(@NotNull Identifier identifier, @NotNull String translateKey) {
        super(identifier, translateKey);
    }

    @Override
    public @NotNull String getIdString() {
        return getId().toString();
    }

    @Override
    protected @NotNull String createTranslateKey(@NotNull Identifier identifier) {
        return identifier.toTranslationKey("key");
    }
}
