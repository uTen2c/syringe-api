package dev.uten2c.syringe.api.message;

import dev.uten2c.syringe.api.SyringeApi;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record MessageContext(@NotNull Component message, boolean shadow, float size, int lineHeight,
                             @NotNull MessagePosition position, int offsetX, int offsetY, long fadein) {
    public static class Builder {
        private @NotNull Component message = Component.empty();
        private boolean shadow = false;
        private float size = 1f;
        private int lineHeight = SyringeApi.DEFAULT_MESSAGE_LINE_HEIGHT;
        private @NotNull MessagePosition position = MessagePosition.TOP_LEFT;
        private int offsetX;
        private int offsetY;
        private long fadein;

        public @NotNull Builder message(@NotNull Component message) {
            this.message = message;
            return this;
        }

        public @NotNull Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public @NotNull Builder size(float size) {
            this.size = size;
            return this;
        }

        public @NotNull Builder lineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public @NotNull Builder position(@NotNull MessagePosition position) {
            this.position = position;
            return this;
        }

        public @NotNull Builder offsetX(int offsetX) {
            this.offsetX = offsetX;
            return this;
        }

        public @NotNull Builder offsetY(int offsetY) {
            this.offsetY = offsetY;
            return this;
        }

        public @NotNull Builder offset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public @NotNull Builder fadein(long fadein) {
            this.fadein = fadein;
            return this;
        }

        public @NotNull MessageContext build() {
            return new MessageContext(message, shadow, size, lineHeight, position, offsetX, offsetY, fadein);
        }
    }
}
