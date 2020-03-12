package com.mactso.hbm;

import com.mactso.hbm.config.MyConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

public class HBMCommand {
	String subcommand = "";
	String value = "";
	
	
	public static void registerold(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("hbmds").requires((source) -> 
			{
				return source.hasPermissionLevel(2);
			}
		)
		.then(
			Commands.argument("digSpeed", DoubleArgumentType.doubleArg(1.0,16.0)).executes(ctx -> {
				System.out.println ("digSpeed = " + DoubleArgumentType.getDouble(ctx, "digSpeed"));
				return setDigSpeed (DoubleArgumentType.getDouble(ctx, "digSpeed"));
				// return 1;
			}
			)));

	}

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("hbm").requires((source) -> 
			{
				return source.hasPermissionLevel(2);
			}
		)
		.then(Commands.literal("digSpeed").then(
			Commands.argument("digSpeed", DoubleArgumentType.doubleArg(1.0,16.0)).executes(ctx -> {
//				System.out.println ("digSpeed = " + DoubleArgumentType.getDouble(ctx, "digSpeed"));
				return setDigSpeed (DoubleArgumentType.getDouble(ctx, "digSpeed"));
				// return 1;
			}
			)
			)
			)
		.then(Commands.literal("debugLevel").then(
				Commands.argument("debugLevel", IntegerArgumentType.integer(0,2)).executes(ctx -> {
//					System.out.println ("debugLevel = " + IntegerType.getInteger(ctx, "debugLevel"));
					return setDebugLevel(IntegerArgumentType.getInteger(ctx, "debugLevel"));
					// return 1;
			}
			)
			)
			)
		.then(Commands.literal("exhaustionType").then(
				Commands.argument("exhaustionType", IntegerArgumentType.integer(0,1)).executes(ctx -> {
//					System.out.println ("exhaustionType = " + IntegerType.getInteger(ctx, "exhaustionType"));
					return setExhaustionType(IntegerArgumentType.getInteger(ctx, "exhaustionType"));
					// return 1;
			}
			)
			)
			)
		);

	}
	
	public static int setDigSpeed (double newDigSpeed) {
			MyConfig.aDigSpeedModifier = newDigSpeed;
			MyConfig.pushValues();
		return 1;
	}

	public static int setDebugLevel (int newDebugLevel) {
		MyConfig.aDebugLevel = newDebugLevel;
		MyConfig.pushValues();
		return 1;
	}
	
	public static int setExhaustionType (int newExhaustionType) {
		MyConfig.aExhaustionType = newExhaustionType;
		MyConfig.pushValues();
		return 1;
	}
}
