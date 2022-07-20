package dev.uten2c.syringe.api;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractSyringeApi {
    static final ConcurrentMap<UUID, SyringeVersion> PLAYER_VERSION_MAP = new ConcurrentHashMap<>();

    public boolean isSyringeUser(@NotNull UUID uniqueId) {
        return PLAYER_VERSION_MAP.containsKey(uniqueId);
    }

    public Optional<SyringeVersion> getSyringeVersion(@NotNull UUID uniqueId) {
        return Optional.ofNullable(PLAYER_VERSION_MAP.get(uniqueId));
    }

    public void resetPlayerStates(@NotNull UUID uniqueId) {
        PLAYER_VERSION_MAP.remove(uniqueId);
    }
}
