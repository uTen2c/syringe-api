package dev.uten2c.syringe.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.uten2c.syringe.api.SyringeApi;
import dev.uten2c.syringe.api.command.argument.HudPartArgumentType;
import dev.uten2c.syringe.api.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.api.command.argument.PerspectiveArgumentType;
import dev.uten2c.syringe.api.message.MessageContext;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SyringeCommand {
    private static final SyringeApi API = SyringeApi.getInstance();
    private static final CommandDispatcher<CommandSourceStack> DISPATCHER = ((CraftServer) Bukkit.getServer()).getHandle().getServer().vanillaCommandDispatcher.getDispatcher();
    private static final String COMMAND_NAME = "syringe";
    private static final Field frozenField;
    private static final Field byClassField;

    static {
        //noinspection OptionalGetWithoutIsPresent
        frozenField = Arrays.stream(MappedRegistry.class.getDeclaredFields())
            .filter(field -> field.getName().equals("ca") || field.getName().equals("frozen"))
            .findFirst()
            .get();
        frozenField.setAccessible(true);
        //noinspection OptionalGetWithoutIsPresent
        byClassField = Arrays.stream(ArgumentTypeInfos.class.getDeclaredFields())
            .filter(field -> field.getName().equals("a") || field.getName().equals("BY_CLASS"))
            .findFirst()
            .get();
        byClassField.setAccessible(true);
    }

    private SyringeCommand() {
    }

    public static void register() {
        DISPATCHER.register(literal(COMMAND_NAME)
            .requires(source -> {
                if (!source.hasPermission(2)) {
                    return false;
                }
                var entity = source.getBukkitEntity();
                return entity == null || (entity instanceof Player player && API.isSyringeUser(player));
            })
            .then(message())
            .then(perspective())
            .then(hud())
        );
        try {
            registerArgumentTypes();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unregister() {
        DISPATCHER.getRoot().removeCommand(COMMAND_NAME);
    }

    private static void registerArgumentTypes() throws IllegalAccessException {
        frozenField.set(Registry.COMMAND_ARGUMENT_TYPE, false);

        registerArgumentType("position", MessagePositionArgumentType.class, MessagePositionArgumentType::messagePosition);
        registerArgumentType("perspective", PerspectiveArgumentType.class, PerspectiveArgumentType::perspective);
        registerArgumentType("hud_part", HudPartArgumentType.class, HudPartArgumentType::hudPart);

        ((MappedRegistry<?>) Registry.COMMAND_ARGUMENT_TYPE).freeze();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> message() {
        var display = literal("display").then(argument("targets", EntityArgument.players()).then(argument("id", StringArgumentType.string()).then(argument("shadow", BoolArgumentType.bool()).then(argument("size", FloatArgumentType.floatArg(0)).then(argument("line height", IntegerArgumentType.integer()).then(argument("position", MessagePositionArgumentType.messagePosition()).then(argument("offset x", IntegerArgumentType.integer()).then(argument("offset y", IntegerArgumentType.integer()).then(argument("fadein", LongArgumentType.longArg()).then(argument("message", ComponentArgument.textComponent()).executes(ctx -> {
            var id = StringArgumentType.getString(ctx, "id");
            var context = new MessageContext.Builder()
                .message(PaperAdventure.asAdventure(ComponentArgument.getComponent(ctx, "message")))
                .shadow(BoolArgumentType.getBool(ctx, "shadow"))
                .size(FloatArgumentType.getFloat(ctx, "size"))
                .lineHeight(IntegerArgumentType.getInteger(ctx, "line height"))
                .position(MessagePositionArgumentType.getMessagePosition(ctx, "position"))
                .offsetX(IntegerArgumentType.getInteger(ctx, "offset x"))
                .offsetY(IntegerArgumentType.getInteger(ctx, "offset y"))
                .fadein(LongArgumentType.getLong(ctx, "fadein"))
                .build();
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.displayMessage(target, id, context));
            return 1;
        })))))))))));
        var discard = literal("discard").then(argument("targets", EntityArgument.players()).then(argument("id", StringArgumentType.string()).then(argument("fadeout", LongArgumentType.longArg(0)).executes(ctx -> {
            var id = StringArgumentType.getString(ctx, "id");
            var fadeout = LongArgumentType.getLong(ctx, "fadeout");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.discardMessage(target, id, fadeout));
            return 1;
        }))));
        var clear = literal("clear").then(argument("targets", EntityArgument.players()).executes(ctx -> {
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(API::clearMessage);
            return 1;
        }));
        return literal("message").then(display).then(discard).then(clear);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> perspective() {
        var set = literal("set").then(argument("targets", EntityArgument.players()).then(argument("perspective", PerspectiveArgumentType.perspective()).executes(ctx -> {
            var perspective = PerspectiveArgumentType.getPerspective(ctx, "perspective");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.setPerspective(target, perspective));
            return 1;
        })));
        var lock = literal("lock").then(argument("targets", EntityArgument.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var isLock = BoolArgumentType.getBool(ctx, "lock");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.lockPerspective(target, isLock));
            return 1;
        })));
        return literal("perspective").then(set).then(lock);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> hud() {
        var hide = literal("hide").then(argument("targets", EntityArgument.players()).then(argument("part", HudPartArgumentType.hudPart()).executes(ctx -> {
            var hudPart = HudPartArgumentType.getHudPart(ctx, "part");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.hideHudParts(target, hudPart));
            return 1;
        })));
        var show = literal("show").then(argument("targets", EntityArgument.players()).then(argument("part", HudPartArgumentType.hudPart()).executes(ctx -> {
            var hudPart = HudPartArgumentType.getHudPart(ctx, "part");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.showHudParts(target, hudPart));
            return 1;
        })));
        return literal("hud").then(hide).then(show);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ArgumentType<?>> void registerArgumentType(String id, Class<T> clazz, Supplier<T> typeSupplier) {
        var loc = new ResourceLocation(id);
        if (Registry.COMMAND_ARGUMENT_TYPE.containsKey(loc)) {
            return;
        }
        try {
            var serializer = SingletonArgumentInfo.contextFree(typeSupplier);
            var classMap = (Map<Class<?>, ArgumentTypeInfo<?, ?>>) byClassField.get(null);
            Registry.register(Registry.COMMAND_ARGUMENT_TYPE, loc, serializer);
            classMap.put(clazz, serializer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
