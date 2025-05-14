package com.mactso.harderbranchmining.commands;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.manager.ToolManager;
import com.mactso.harderbranchmining.utility.Utility;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
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
		.then(Commands.literal("downSpeed").then(
				Commands.argument("downSpeed", IntegerArgumentType.integer(0,100)).executes(ctx -> {
					return setDownSpeed (IntegerArgumentType.getInteger(ctx, "downSpeed"));
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
					Level level = serverPlayerEntity.level();
					Item tempItem = serverPlayerEntity.getMainHandItem().getItem();
					
					ResourceKey<Level> dimensionKey = level.dimension();
					String dimensionId = dimensionKey.location().toString();
	
					DefaultedRegistry<Item> itemRegistry = BuiltInRegistries.ITEM;
					// Registry<Item> itemRegistry =  dynreg.registryOrThrow(Registries.ITEM);
					ResourceLocation key = itemRegistry.getKey(tempItem);
					ToolManager.toolItem toolInfo = ToolManager.getToolInfo(key, dimensionId);
					float depthFactor = (float) Utility.calcDepthFactor(serverPlayerEntity.blockPosition().getY(), toolInfo);
					if (depthFactor == -1) depthFactor = 0;
					String chatMessage = "\n HarderBranchMining Info";
					Utility.sendChat(serverPlayerEntity, chatMessage, ChatFormatting.DARK_GREEN);
		            chatMessage = 
  	            		    "  Debug Level...................: " + MyConfig.getDebugLevel()
		            		+ "\n  Exhaustion Type........: " + MyConfig.getExhaustionTypeAsString() + "(" +MyConfig.getExhaustionType() + ")"
		            		+ "\n  Extra Down Modifier: " + MyConfig.getDownModifierAsString()
		            		+ "\n  Dimension..........................: " + dimensionId
		            		+ "\n  Tool Top Y.......................: " + toolInfo.getYModifierStart()
		            	    + " -> Player Y: " + serverPlayerEntity.blockPosition().getY()
		            	    + " -> Tool Bottom Y : " + toolInfo.getYModifierStop()
		            		+ "\n  Tool Dig Modifier........: " + toolInfo.getDigModifierAsPercent() +" * " + 
		            	    Utility.formatPercentage(depthFactor) + " = " + Utility.formatPercentage((float)(depthFactor*toolInfo.getDigModifier())) + " slower."
		            		+ "\n  Tool Exhaustion..........: " + toolInfo.getExhaustionAmount()+" * " + 
		            	    Utility.formatPercentage(depthFactor)+ " = " + (float)(depthFactor*toolInfo.getExhaustionAmount()) + " extra exhaustion.";
		            		;
					Utility.sendChat(serverPlayerEntity, chatMessage);
		            return 1;
			}
			)
			)		
		);

	}
	
	public static int setDownSpeed (int newDownSpeed) {
		MyConfig.downModifier = newDownSpeed;
		MyConfig.pushValues();
	return 1;
	}	
	
	public static int setDebugLevel (int newDebugLevel) {
		MyConfig.debugLevel = newDebugLevel;
		MyConfig.pushValues();
		return 1;
	}
	
	public static int setExhaustionType (int newExhaustionType) {
		MyConfig.exhaustionType = newExhaustionType;
		MyConfig.pushValues();
		return 1;
	}
}
