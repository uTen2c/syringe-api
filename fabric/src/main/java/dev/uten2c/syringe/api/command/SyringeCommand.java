package dev.uten2c.syringe.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.uten2c.syringe.api.SyringeApi;
import dev.uten2c.syringe.api.command.argument.HudPartArgumentType;
import dev.uten2c.syringe.api.command.argument.MessagePositionArgumentType;
import dev.uten2c.syringe.api.command.argument.PerspectiveArgumentType;
import dev.uten2c.syringe.api.message.MessageContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SyringeCommand {
    private static final SyringeApi API = SyringeApi.getInstance();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("syringe")
            .requires(source -> {
                if (!source.hasPermissionLevel(2)) {
                    return false;
                }
                var entity = source.getEntity();
                return entity == null || (entity instanceof ServerPlayerEntity player && API.isSyringeUser(player));
            })
            .then(message())
            .then(perspective())
            .then(hud())
            .then(camera())
            .then(movement())
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> message() {
        var display = literal("display").then(argument("targets", EntityArgumentType.players()).then(argument("id", StringArgumentType.string()).then(argument("shadow", BoolArgumentType.bool()).then(argument("size", FloatArgumentType.floatArg(0)).then(argument("line height", IntegerArgumentType.integer()).then(argument("position", MessagePositionArgumentType.messagePosition()).then(argument("offset x", IntegerArgumentType.integer()).then(argument("offset y", IntegerArgumentType.integer()).then(argument("fadein", LongArgumentType.longArg()).then(argument("message", TextArgumentType.text()).executes(ctx -> {
            var id = StringArgumentType.getString(ctx, "id");
            var context = new MessageContext.Builder()
                .message(TextArgumentType.getTextArgument(ctx, "message"))
                .shadow(BoolArgumentType.getBool(ctx, "shadow"))
                .size(FloatArgumentType.getFloat(ctx, "size"))
                .lineHeight(IntegerArgumentType.getInteger(ctx, "line height"))
                .position(MessagePositionArgumentType.getMessagePosition(ctx, "position"))
                .offsetX(IntegerArgumentType.getInteger(ctx, "offset x"))
                .offsetY(IntegerArgumentType.getInteger(ctx, "offset y"))
                .fadein(LongArgumentType.getLong(ctx, "fadein"))
                .build();
            EntityArgumentType.getPlayers(ctx, "targets")
                .forEach(target -> API.displayMessage(target, id, context));
            return 1;
        })))))))))));
        var discard = literal("discard").then(argument("targets", EntityArgumentType.players()).then(argument("id", StringArgumentType.string()).then(argument("fadeout", LongArgumentType.longArg(0)).executes(ctx -> {
            var id = StringArgumentType.getString(ctx, "id");
            var fadeout = LongArgumentType.getLong(ctx, "fadeout");
            EntityArgumentType.getPlayers(ctx, "targets")
                .forEach(target -> API.discardMessage(target, id, fadeout));
            return 1;
        }))));
        var clear = literal("clear").then(argument("targets", EntityArgumentType.players()).executes(ctx -> {
            EntityArgumentType.getPlayers(ctx, "targets").forEach(API::clearMessage);
            return 1;
        }));
        return literal("message").then(display).then(discard).then(clear);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> perspective() {
        var set = literal("set").then(argument("targets", EntityArgumentType.players()).then(argument("perspective", PerspectiveArgumentType.perspective()).executes(ctx -> {
            var perspective = PerspectiveArgumentType.getPerspective(ctx, "perspective");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.setPerspective(target, perspective));
            return 1;
        })));
        var lock = literal("lock").then(argument("targets", EntityArgumentType.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var isLock = BoolArgumentType.getBool(ctx, "lock");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.lockPerspective(target, isLock));
            return 1;
        })));
        return literal("perspective").then(set).then(lock);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> hud() {
        var hud = literal("hud");
        var hide = literal("hide").then(argument("targets", EntityArgumentType.players()).then(argument("part", HudPartArgumentType.hudPart()).executes(ctx -> {
            var hudPart = HudPartArgumentType.getHudPart(ctx, "part");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.hideHudParts(target, hudPart));
            return 1;
        })));
        var show = literal("show").then(argument("targets", EntityArgumentType.players()).then(argument("part", HudPartArgumentType.hudPart()).executes(ctx -> {
            var hudPart = HudPartArgumentType.getHudPart(ctx, "part");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.showHudParts(target, hudPart));
            return 1;
        })));
        return hud.then(hide).then(show);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> camera() {
        var setDirection = literal("set_direction").then(argument("targets", EntityArgumentType.players()).then(argument("relative", BoolArgumentType.bool()).then(argument("yaw", FloatArgumentType.floatArg()).then(argument("pitch", FloatArgumentType.floatArg()).executes(ctx -> {
            var relative = BoolArgumentType.getBool(ctx, "relative");
            var yaw = FloatArgumentType.getFloat(ctx, "yaw");
            var pitch = FloatArgumentType.getFloat(ctx, "pitch");
            EntityArgumentType.getPlayers(ctx, "targets")
                .forEach(target -> API.setDirection(target, relative, yaw, pitch));
            return 1;
        })))));
        var zoom = literal("zoom").then(argument("targets", EntityArgumentType.players()).then(argument("multiplier", FloatArgumentType.floatArg()).executes(ctx -> {
            var multiplier = FloatArgumentType.getFloat(ctx, "multiplier");
            EntityArgumentType.getPlayers(ctx, "targets")
                .forEach(target -> API.zoom(target, multiplier));
            return 1;
        })));
        var lock = literal("lock").then(argument("targets", EntityArgumentType.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var isLock = BoolArgumentType.getBool(ctx, "lock");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.lockCamera(target, isLock));
            return 1;
        })));
        return literal("camera").then(setDirection).then(zoom).then(lock);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> movement() {
        var lock = literal("lock").then(argument("targets", EntityArgumentType.players()).then(argument("lock", BoolArgumentType.bool()).executes(ctx -> {
            var isLock = BoolArgumentType.getBool(ctx, "lock");
            EntityArgumentType.getPlayers(ctx, "targets").forEach(target -> API.lockMovement(target, isLock));
            return 1;
        })));
        return literal("movement").then(lock);
    }
}
