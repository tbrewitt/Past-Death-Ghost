package com.pastdeathghost.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pastdeathghost.ghost.GhostManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GhostCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("ghosts")
                    .then(literal("clear")
                            .executes(context -> {
                                GhostManager.getInstance().clearGhosts();
                                context.getSource().sendFeedback(Text.literal("All past death ghosts have been cleared.").formatted(Formatting.GREEN));
                                return 1;
                            }))
                    .then(literal("limit")
                            .then(argument("amount", IntegerArgumentType.integer(1))
                                    .executes(context -> {
                                        int amount = IntegerArgumentType.getInteger(context, "amount");
                                        GhostManager.getInstance().setMaxGhosts(amount);
                                        context.getSource().sendFeedback(Text.literal("Maximum death ghosts limit updated to: " + amount).formatted(Formatting.GREEN));
                                        return 1;
                                    }))
                            .executes(context -> {
                                int current = GhostManager.getInstance().getMaxGhosts();
                                context.getSource().sendFeedback(Text.literal("Current maximum death ghosts limit: " + current).formatted(Formatting.GREEN));
                                return 1;
                            }))
            );
        });
    }
}
