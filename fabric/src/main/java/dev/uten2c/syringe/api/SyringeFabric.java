package dev.uten2c.syringe.api;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.uten2c.syringe.api.command.SyringeCommand;
import dev.uten2c.syringe.api.command.argument.HudPartArgumentType;
import dev.uten2c.syringe.api.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.api.command.argument.PerspectiveArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class SyringeFabric implements ModInitializer, PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        SyringeApi.setInstance(new SyringeApiImpl());
    }

    @Override
    public void onInitialize() {
        FabricEventListener.setup();
        SyringeNetworking.setup();
        CommandRegistrationCallback.EVENT.register(SyringeCommand::register);
        registerArgumentType("position", MessagePositionArgumentType.class, MessagePositionArgumentType::messagePosition);
        registerArgumentType("perspective", PerspectiveArgumentType.class, PerspectiveArgumentType::perspective);
        registerArgumentType("hud_part", HudPartArgumentType.class, HudPartArgumentType::hudPart);
    }

    private static <T extends ArgumentType<?>> void registerArgumentType(String id, Class<T> clazz, Supplier<T> typeSupplier) {
        ArgumentTypeRegistry.registerArgumentType(
            new Identifier(SyringeNetworking.NAMESPACE, id),
            clazz,
            ConstantArgumentSerializer.of(typeSupplier)
        );
    }
}
