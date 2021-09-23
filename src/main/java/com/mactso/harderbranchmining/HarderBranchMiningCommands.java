package com.mactso.harderbranchmining;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class HarderBranchMiningCommands {
	String subcommand = "";
	String value = "";
	
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("hbm").requires((source) -> 
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
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) ctx.getSource().getEntity();
					World worldName = serverPlayerEntity.level;
					String chatMessage = worldName.dimensionType().toString() 
										+ "\n Current Values";
					MyConfig.sendChat(serverPlayerEntity, chatMessage, TextFormatting.DARK_GREEN, MyConfig.BOLD);
		            chatMessage = 
		              		  "\n  Exhaustion Type.: " + MyConfig.aExhaustionType
		            		+ "\n  Debug Level...........: " + MyConfig.aDebugLevel
		            		+ "\n  Dig Modifier.............: " + MyConfig.aDigSpeedModifier
		            		+ "\n  Down Modifier........: " + MyConfig.aDownSpeedModifier
		            		;
					MyConfig.sendChat(serverPlayerEntity, chatMessage);
		            return 1;
			}
			)
			)		
		);

	}
	
	public static int setDigSpeed (double newDigSpeed) {
			MyConfig.aDigSpeedModifier = newDigSpeed;
			MyConfig.pushValues();
		return 1;
	}

	public static int setDownSpeed (double newDownSpeed) {
		MyConfig.aDownSpeedModifier = newDownSpeed;
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
