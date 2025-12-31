package com.mactso.harderbranchmining.common;

import java.lang.reflect.Field;

import org.jspecify.annotations.Nullable;

import com.mactso.harderbranchmining.common.managers.ToolManager;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Exhaustion {
	
    private static Field exhaustionField;

    private static final int EXHAUSTION_NONE = 0;
    // private static final int EXHAUSTION_FIXED = 1;
    private static final int EXHAUSTION_BY_DEPTH = 2;
    
    static {
    	try {
    		exhaustionField = FoodData.class.getDeclaredField("exhaustionLevel"); // Replace with actual obfuscated name
    		exhaustionField.setAccessible(true);
    	} catch (NoSuchFieldException e ) {
            MyUtilities.debugMsg(0, "[HarderBranchMining] WARNING: Could not access FoodData.exhaustionLevel field. Exhaustion tracking may not work correctly!");
            MyUtilities.debugMsg(0, e.toString());
    	}
    }
    
    // this is executed only on the server side.  no visuals and server owns FoodData
	public static void applyExhaustionForBlockBreak(ServerPlayer player, BlockState targetBlockState, BlockPos pos) {

		if (MyConfig.getExhaustionType() == EXHAUSTION_NONE)
			return;
		
		ServerLevel level = player.level();
		
		float destroySpeed = targetBlockState.getDestroySpeed(level, pos);
		if (destroySpeed < 1.0f) {
		    if (MyConfig.debugLevel > 1) {
		        MyUtilities.sendChat(player, "Block Broken! Soft Block. No Exhaustion.", ChatFormatting.GOLD);
		    }
		    return;
		}

		ResourceKey<Level> dimensionKey = level.dimension();
		String dimensionId = dimensionKey.identifier().toString();
		FoodData foodData = player.getFoodData();
		RegistryAccess registryAccess = level.registryAccess();
		Registry<Block> blockRegistry = registryAccess.lookupOrThrow(Registries.BLOCK);
		Registry<Item> itemRegistry = registryAccess.lookupOrThrow(Registries.ITEM);

		// World debugWorldValue = (World) event.getWorld();
		// ItemStack debugItemStack = event.getPlayer().getHeldItemMainhand();
		// ---- tool + block context ----
		ItemStack toolStack = player.getMainHandItem();
		Item toolInMainHand = toolStack.getItem();
		 @Nullable
		Identifier toolKey = itemRegistry.getKey(toolInMainHand);
		
		ToolManager.toolItem toolInfo = ToolManager.getToolInfoOrDefault(toolKey, dimensionId);

		if (pos.getY() > toolInfo.getYModifierStart())
			return;

		if (Tests.isSoftBlock(player, pos, targetBlockState) || (Tests.isSkipBlock("calc Exhaustion", player, targetBlockState.getBlock()))) {
			return;
		}

		double addedExhaustion = toolInfo.getExhaustionAmount();

		if (MyConfig.getExhaustionType() == EXHAUSTION_BY_DEPTH) {
			double depthFactor = MyUtilities.calcDepthFactor(toolInfo, pos.getY());
			addedExhaustion *= depthFactor;
		}

		foodData.addExhaustion((float) addedExhaustion);

		if (MyConfig.debugLevel > 0) {
			exhaustionDebugHelper(player, targetBlockState, pos, dimensionId, foodData, blockRegistry, toolKey,
					addedExhaustion);
		}
	}

	
	public static float getExhaustionLevel(FoodData fd) {
		
		if (exhaustionField == null) 
				return -1.0F;
		try {
			return exhaustionField.getFloat(fd);
		} catch ( IllegalAccessException e) {
			e.printStackTrace();
			return -1.0f;
		}
	}
	
	private static void exhaustionDebugHelper(ServerPlayer player, BlockState targetBlockState, BlockPos pos,
			String dimensionId, FoodData foodData, Registry<Block> blockRegistry, Identifier toolKey,
			double addedExhaustion) {
		Identifier blockRL = blockRegistry.getKey(targetBlockState.getBlock());
		MyUtilities.debugMsg(1,
				"Block " + blockRL + " Broken! Player:" + player.getName().getString()
						+ ", Dimension:" + dimensionId + ", Pos:" + pos + ", addedExhaustion:" + addedExhaustion);
		if (MyConfig.debugLevel > 1) {
			MyUtilities.sendChat(player, "\nBlock " + blockRL + " Broken! Player: "
					+ player.getName().getString() + " with " + toolKey,
					ChatFormatting.GOLD);
			MyUtilities.sendChat(player,
					"Dimension   :" + dimensionId + " at altitude     :" + pos.getY() + "\nExtra Exhaustion:"
							+ Math.round(addedExhaustion * 1000.0) / 1000.0 + "\nSaturation Now: "
							+ foodData.getSaturationLevel() + " Exhaustion Now: " + getExhaustionLevel(foodData),
					ChatFormatting.GREEN);
		}
	}

}
