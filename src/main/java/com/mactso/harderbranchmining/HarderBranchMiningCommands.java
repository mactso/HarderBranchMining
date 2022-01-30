package com.mactso.harderbranchmining;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class HarderBranchMiningCommands {
	String subcommand = "";
	String value = "";
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("harderbranchmining").requires((source) -> 
			{
				return source.hasPermission(2);
			}
		)
		.then(Commands.literal("digSpeed").then(
			Commands.argument("digSpeed", DoubleArgumentType.doubleArg(1.0,16.0)).executes(ctx -> {
				return setDigSpeed (DoubleArgumentType.getDouble(ctx, "digSpeed"));
				// return 1;
			}
			)
			)
			)
		.then(Commands.literal("downSpeed").then(
				Commands.argument("downSpeed", DoubleArgumentType.doubleArg(1.0,16.0)).executes(ctx -> {
					return setDownSpeed (DoubleArgumentType.getDouble(ctx, "downSpeed"));
					// return 1;
			}
			)
			)
			)
		.then(Commands.literal("debugLevel").then(
				Commands.argument("debugLevel", IntegerArgumentType.integer(0,2)).executes(ctx -> {
					return setDebugLevel(IntegerArgumentType.getInteger(ctx, "debugLevel"));
					// return 1;
			}
			)
			)
			)
		.then(Commands.literal("exhaustionType").then(
				Commands.argument("exhaustionType", IntegerArgumentType.integer(0,1)).executes(ctx -> {
					return setExhaustionType(IntegerArgumentType.getInteger(ctx, "exhaustionType"));
					// return 1;
			}
			)
			)
			)
		.then(Commands.literal("info").executes(ctx -> {
					ServerPlayer serverPlayerEntity = (ServerPlayer) ctx.getSource().getEntity();
					Level worldName = serverPlayerEntity.level;
					String chatMessage = worldName.dimensionType().toString() 
										+ "\n Current Values";
					MyConfig.sendChat(serverPlayerEntity, chatMessage, ChatFormatting.DARK_GREEN, MyConfig.BOLD);
		            chatMessage = 
		              		  "\n  Exhaustion Type.: " + MyConfig.aExhaustionType
		            		+ "\n  Debug Level...........: " + MyConfig.aDebugLevel
		            		+ "\n  Dig Modifier.............: " + MyConfig.aDigModifier
		            		+ "\n  Down Modifier........: " + MyConfig.aDownModifier
		            		;
					MyConfig.sendChat(serverPlayerEntity, chatMessage);
		            return 1;
			}
			)
			)		
		);

	}
	
	public static int setDigSpeed (double newDigSpeed) {
			MyConfig.aDigModifier = newDigSpeed;
			MyConfig.pushValues();
		return 1;
	}

	public static int setDownSpeed (double newDownSpeed) {
		MyConfig.aDownModifier = newDownSpeed;
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
