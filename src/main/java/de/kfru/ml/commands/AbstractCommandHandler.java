package de.kfru.ml.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Getter
public abstract class AbstractCommandHandler {

  protected final LiteralArgumentBuilder<ServerCommandSource> command;

  public AbstractCommandHandler(final String commandName) {
    this(commandName, new ArrayList<>(), new ArrayList<>());
  }

  public AbstractCommandHandler(final String commandName, final List<RequiredArgumentBuilder<ServerCommandSource, ?>> requiredArguments) {
    this(commandName, requiredArguments, new ArrayList<>());
  }

  public AbstractCommandHandler(final String commandName, final List<RequiredArgumentBuilder<ServerCommandSource, ?>> requiredArguments, final List<RequiredArgumentBuilder<ServerCommandSource, ?>> optionalArguments) {
    final LiteralArgumentBuilder<ServerCommandSource> root = LiteralArgumentBuilder.literal(commandName);

    // Only runnable without args if there are no required args
    if (requiredArguments.isEmpty()) {
      root.executes(this::handle);
    }

    ArgumentBuilder<ServerCommandSource, ?> parent = root;

    // Required chain: only the last required arg should execute
    for (int i = 0; i < requiredArguments.size(); i++) {
      RequiredArgumentBuilder<ServerCommandSource, ?> arg = requiredArguments.get(i);
      boolean isLeaf = (i == requiredArguments.size() - 1) && optionalArguments.isEmpty();
      if (isLeaf) {
        arg.executes(this::handle);
      }
      parent.then(arg);
      parent = arg;
    }

    // Optional chain: after each optional arg, allow execution
    for (int i = 0; i < optionalArguments.size(); i++) {
      RequiredArgumentBuilder<ServerCommandSource, ?> arg = optionalArguments.get(i);
      arg.executes(this::handle);
      parent.then(arg);
      parent = arg;
    }

    // If there were required args and optional args exist, ensure leaf of required chain can execute too
    if (!requiredArguments.isEmpty() && optionalArguments.isEmpty()) {
      // handled above via isLeaf
    } else if (!requiredArguments.isEmpty() && !optionalArguments.isEmpty()) {
      // leaf execution already set on each optional arg; first optional makes it runnable
    } else if (requiredArguments.isEmpty() && !optionalArguments.isEmpty()) {
      // root already executable; each optional executable too
    }

    this.command = root;
  }

  public abstract int handle(final CommandContext<ServerCommandSource> context);

  public <T> T getArgumentValue(final CommandContext<ServerCommandSource> context, final String name, final BiFunction<CommandContext<ServerCommandSource>, String, T> getValueFn, final boolean optional) {
    T value;
      try {
        value = getValueFn.apply(context, name);
      } catch (Exception e) {
        if (optional) {
          return null;
        }
        throw new RuntimeException(e);
      }
      if (value == null && !optional) {
        throw new IllegalStateException("Required argument '" + name + "' is null.");
      }
      return value;
  }
}
