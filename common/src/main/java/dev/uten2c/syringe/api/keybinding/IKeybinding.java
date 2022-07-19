package dev.uten2c.syringe.api.keybinding;

import org.jetbrains.annotations.NotNull;

public interface IKeybinding {
    @NotNull String getIdString();

    @NotNull String getTranslateKey();

    @NotNull KeyCode getKeyCode();
}

