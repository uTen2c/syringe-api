package dev.uten2c.syringe.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.uten2c.syringe.api.SyringeApi;
import dev.uten2c.syringe.api.message.MessageContext;
import dev.uten2c.syringe.api.message.MessagePosition;
import dev.uten2c.syringe.api.perspective.Perspective;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SyringeCommand {
    private static final SyringeApi API = SyringeApi.getInstance();
    private static final CommandDispatcher<CommandSourceStack> DISPATCHER = ((CraftServer) Bukkit.getServer()).getHandle().getServer().vanillaCommandDispatcher.getDispatcher();
    private static final String COMMAND_NAME = "syringe";

    private SyringeCommand() {
    }

    public static void register() {
        DISPATCHER.register(literal(COMMAND_NAME)
            .then(message())
            .then(perspective())
        );
    }

    public static void unregister() {
        DISPATCHER.getRoot().removeCommand(COMMAND_NAME);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> message() {
        var lineHeight = argument("line height", IntegerArgumentType.integer());
        for (var position : MessagePosition.values()) {
            lineHeight.then(literal(position.name().toLowerCase()).then(argument("offset x", IntegerArgumentType.integer()).then(argument("offset y", IntegerArgumentType.integer()).then(argument("fadein", LongArgumentType.longArg()).then(argument("message", ComponentArgument.textComponent()).executes(ctx -> {
                var id = StringArgumentType.getString(ctx, "id");
                var context = new MessageContext.Builder()
                    .message(PaperAdventure.asAdventure(ComponentArgument.getComponent(ctx, "message")))
                    .shadow(BoolArgumentType.getBool(ctx, "shadow"))
                    .size(FloatArgumentType.getFloat(ctx, "size"))
                    .lineHeight(IntegerArgumentType.getInteger(ctx, "line height"))
                    .position(position)
                    .offsetX(IntegerArgumentType.getInteger(ctx, "offset x"))
                    .offsetY(IntegerArgumentType.getInteger(ctx, "offset y"))
                    .fadein(LongArgumentType.getLong(ctx, "fadein"))
                    .build();
                EntityArgument.getPlayers(ctx, "targets").stream()
                    .map(ServerPlayer::getBukkitEntity)
                    .forEach(target -> API.displayMessage(target, id, context));
                return 1;
            }))))));
        }
        var display = literal("display").then(argument("targets", EntityArgument.players()).then(argument("id", StringArgumentType.string()).then(argument("shadow", BoolArgumentType.bool()).then(argument("size", FloatArgumentType.floatArg(0)).then(lineHeight)))));
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
        var t = argument("targets", EntityArgument.players());
        for (var perspective : Perspective.values()) {
            t.then(literal(perspective.name().toLowerCase()).executes(ctx -> {
                EntityArgument.getPlayers(ctx, "targets").stream()
                    .map(ServerPlayer::getBukkitEntity)
                    .forEach(target -> API.setPerspective(target, perspective));
                return 1;
            }));
        }
        var set = literal("set").then(t);
        var lock = literal("lock").then(argument("targets", EntityArgument.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var isLock = BoolArgumentType.getBool(ctx, "lock");
            EntityArgument.getPlayers(ctx, "targets").stream()
                .map(ServerPlayer::getBukkitEntity)
                .forEach(target -> API.lockPerspective(target, isLock));
            return 1;
        })));
        return literal("perspective").then(set).then(lock);
    }
}
