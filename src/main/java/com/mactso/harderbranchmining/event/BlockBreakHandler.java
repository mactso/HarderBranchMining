//1.15.2-2.0.0.3
package com.mactso.harderbranchmining.event;

import java.lang.reflect.Field;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.manager.IgnoreBlocksListManager;
import com.mactso.harderbranchmining.manager.ToolManager;
import com.mactso.harderbranchmining.utility.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class BlockBreakHandler {

	private static int debugLimiter = 0;
    private static Field exhaustionField;

    static {
    	try {
    		exhaustionField = FoodData.class.getDeclaredField("exhaustionLevel"); // Replace with actual obfuscated name
    		exhaustionField.setAccessible(true);
    	} catch (NoSuchFieldException e ) {
    		e.printStackTrace();
    	}
    }
	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {

		if (MyConfig.exhaustionType == MyConfig.EXHAUSTION_OFF) {
			if (MyConfig.debugLevel > 0) {
				System.out.println("Exhaustion is turned off");
			}
			return;
		}

		if (event.getPlayer() == null) {
			return;
		} else if (event.getPlayer().isCreative()) {
			return;
		}

		Player p = event.getPlayer();

		// no exhaustion for soft items.

		// uncomment for easier debugging.
		// Item item = p.getHeldItemMainhand().getItem();
		// float hardness = event.getState().getDestroySpeed(event.getLevel(), event.getPos());
		
		if (1 >= event.getState().getDestroySpeed(event.getLevel(), event.getPos())) {

			if (MyConfig.debugLevel > 1) {
				if (p instanceof ServerPlayer sp) {
					Utility.sendChat(sp, "Block Broken! Soft Block.  No Exhaustion.", ChatFormatting.GOLD);
				}
			}
			return;
		}

		// World debugWorldValue = (World) event.getWorld();
		// ItemStack debugItemStack = event.getPlayer().getHeldItemMainhand();
		Item tempItem = p.getMainHandItem().getItem();

		// domain:tool:dimension

		ResourceKey<Level> dimensionKey = p.level().dimension();
		String dimensionId = dimensionKey.location().toString();

		// ResourceLocation itemKey = tempItem.builtInRegistryHolder().key().location(); deprecated

		ToolManager.toolItem toolInfo = ToolManager.getToolInfo(BuiltInRegistries.ITEM.getDefaultKey(), dimensionId);

		if (event.getPos().getY() > toolInfo.getYModifierStart()) 
			return;

		if (isSoftBlock(event.getPos(), event.getState(), p)
				|| (isSkipBlock("calc Exhaustion", p, event.getState().getBlock()))) {
			return;
		}

		double depthFactor = Utility.calcDepthFactor(event.getPos().getY(), toolInfo);
		double extraExhaustion = toolInfo.getExhaustionAmount();

		if (MyConfig.getExhaustionType() == 0)
			return;
		if (MyConfig.getExhaustionType() == 1)
			extraExhaustion *= depthFactor;

		FoodData foodData = event.getPlayer().getFoodData();
		foodData.addExhaustion((float) extraExhaustion);

		if (MyConfig.debugLevel > 0) {
			System.out.println("Block Broken! Player:" + p.getName().getString().toString() + ", Dimension:"
					+ dimensionId + ", Pos:" + event.getPos() + ", extraExhaustion:" + extraExhaustion);
			if (MyConfig.debugLevel > 1) {
				if (p instanceof ServerPlayer sp) {
					Utility.sendChat(sp, "\nBlock Broken! with " + BuiltInRegistries.ITEM.getDefaultKey().toString(), ChatFormatting.GOLD);
					Utility.sendChat(sp,
							"Dimension   :" + dimensionId + " at Depth       :" + event.getPos().getY()
									+ "\nExtra Exhaustion:" + Math.round(extraExhaustion * 1000.0) / 1000.0
									+ "\nSaturation Now: " + foodData.getSaturationLevel() + " Exhaustion Now: "
									+ getExhaustionLevel(foodData),
							ChatFormatting.GREEN);
				}
			}
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

	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
		double depthFactor = 0.0;
		String debugWorldName = "server-local ";

		if (event.getEntity() == null) {
			return;
		} else if (event.getEntity().isCreative()) {
			return;
		}

		Player p = event.getEntity();
		if (p.level().isClientSide()) {
			debugWorldName = "client-remote ";
		}

		Item playerItem = p.getMainHandItem().getItem();

//    	if (!playerItem.canHarvestBlock(p.getHeldItemMainhand(), event.getState())) {
//    		return;
//    	}
		boolean toolHarvestsBlockFaster = false;
		float originalToolSpeed = event.getOriginalSpeed();

		if (originalToolSpeed > 1.0f) {
			toolHarvestsBlockFaster = true;
		}

		// normal mining speed for ore blocks? default = true.
		Block block = event.getState().getBlock();

		// no exhaustion for whitelist items.
		// no slowdown for soft items.

		if (isSoftBlock(event.getPosition().get(), event.getState(), p) || (isSkipBlock(debugWorldName, p, block))) {
			return;
		}

		// key = moddomain:tool,dimension

		ResourceKey<Level> dimensionKey = p.level().dimension();
		String dimensionId = dimensionKey.location().toString();

		@SuppressWarnings("deprecation")
		ResourceLocation itemKey = playerItem.builtInRegistryHolder().key().location();

		ToolManager.toolItem toolInfo = ToolManager.getToolInfo(itemKey, dimensionId);

		depthFactor = Utility.calcDepthFactor(event.getPosition().get().getY(), toolInfo);

		if (depthFactor == -1.0) {
			return;
		}

		double newDestroySpeed = calcNewDestroySpeed(event, depthFactor, p, toolInfo);

		if (newDestroySpeed > 0) {
			event.setNewSpeed((float) newDestroySpeed);
		}

		if (MyConfig.debugLevel > 0) {
			if (debugLimiter++ > 5) { // debugLimiter to avoid spamming chat window
				logDebugInfo(event, block, depthFactor, p, dimensionId, itemKey, toolInfo);

				chatDebugInfo(event,  depthFactor, p, dimensionId, itemKey, toolInfo);

				debugLimiter = 0;
			}

		}
	}

	private double calcNewDestroySpeed(PlayerEvent.BreakSpeed event, double depthFactor, Player p,
			ToolManager.toolItem toolInfo) {

		double newDestroySpeed = event.getOriginalSpeed();

		if (toolInfo.getDigModifier() >= 0) {
			newDestroySpeed -= newDestroySpeed * depthFactor * toolInfo.getDigModifier();
		}

		if (event.getPosition().get().getY() < p.getY() && MyConfig.downModifier >= 0) {
			newDestroySpeed -= newDestroySpeed * depthFactor * (MyConfig.downModifier * 0.01);
		}
		return newDestroySpeed;
	}

	private boolean isSoftBlock(BlockPos pos, BlockState s, Player p) {

		double hardness = s.getDestroySpeed(p.level(), pos);
		// Block debugBlock = event.getState().getBlock();
		if (hardness > 1.0) {
			return false;
		}
		return true;
	}

	private boolean isSkipBlock(String debugWorldName, Player p, Block block) {
		if (IgnoreBlocksListManager.ignoreBlocksListHashSet.contains(block)) {
			if (MyConfig.debugLevel > 1) {
				if (p instanceof ServerPlayer sp) {
					Utility.sendChat(sp, debugWorldName + ", Breaking Whitelist Block at normal speed.");
				}
			}
			return true;
		}
		// f230235_a_ == ".contains()"

		if (block.defaultBlockState().is(Tags.Blocks.ORES)) {
			if ((MyConfig.debugLevel > 1) && (debugLimiter++ > 39)) {
				if (p instanceof ServerPlayer sp) {
					Utility.sendChat(sp, block.toString() + " is in the Ore block tags.");
				}
				debugLimiter = 0;
			}
			return true;
		}
		// no exhaustion for ore block items.
		if ((block instanceof RedStoneOreBlock) || (block.defaultBlockState().is(Tags.Blocks.ORES))) {
			if (MyConfig.normalOreHandling) {
				if ((MyConfig.debugLevel > 1) && (debugLimiter++ > 39)) {
					if (p instanceof ServerPlayer sp) {
						Utility.sendChat(sp, "Breaking Ore Block full speed with no speed adjust true.");
					}
					debugLimiter = 0;
				}
				return true;
			}
		}
		return false;
	}

	private void chatDebugInfo(PlayerEvent.BreakSpeed event, double depthFactor, Player p, String dimensionId,
			ResourceLocation key, ToolManager.toolItem toolInfo) {
		Utility.debugMsg(2, p, " ");
		Utility.debugMsg(2, p,
				key.toString() + ":" + dimensionId + " \nExtra Exhaustion:" + toolInfo.getExhaustionAmount() + " ");
		String depthFactorF = String.format("%5.2f%%", 100 * depthFactor);
		Utility.debugMsg(2, p,
				"Y altitude Info (" + toolInfo.getYModifierStart() + " -> " + event.getPosition().get().getY() + " -> "
						+ toolInfo.getYModifierStop() + ") giving " + depthFactorF + " of modifiers.");

		double toolDigMod = (toolInfo.getDigModifier()) * depthFactor;
		String toolDigModF = String.format("%5.2f%%", 100 * toolDigMod);
		Utility.debugMsg(2, p, "Tool DigMod : " + toolInfo.getDigModifierAsPercent() + " Actually Slowed ("
				+ toolDigModF + ") at Y=" + event.getPosition().get().getY() + ").");

		if (event.getPosition().get().getY() < p.getY()) {
			String globalDownRawF = MyConfig.getDownModifierAsString();
			String globalDownModF = String.format("%5.2f%%", MyConfig.getDownModifier() * depthFactor);
			Utility.debugMsg(2, p, " Down Modifier: " + globalDownRawF + " Slowed (" + globalDownModF + ") more.");

		}
		Utility.debugMsg(2, p, "Starting Breaking Speed: " + event.getOriginalSpeed() + " Final Breaking Speed: "
				+ event.getNewSpeed());

	}

	private void logDebugInfo(PlayerEvent.BreakSpeed event, Block b, double depthFactor, Player p, String dimensionId,
			ResourceLocation toolkey, ToolManager.toolItem toolInfo) {
		Utility.debugMsg(1, "\nMining " + BuiltInRegistries.BLOCK.getKey(b).getPath() + " with " +
				toolkey.getPath() +" at " 
				+ " ( x=" + event.getPosition().get().getX() +", y="+event.getPosition().get().getY() +", z="+ event.getPosition().get().getZ() + ") "   
				+ " in the " + dimensionId 
				+ "\n"+toolkey.getPath() +" slows from (" + toolInfo.getYModifierStart() + "y to slowest speed below "
						 + toolInfo.getYModifierStop() + "y ) " 
						+ "\nExtra Exhaustion applied for "+ toolkey.getPath() + " at this depth is :" + toolInfo.getExhaustionAmount());

		Utility.debugMsg(1, "Tool Basic Digging Modifier: -" + toolInfo.getDigModifierAsPercent() + ".");
		if (event.getPosition().get().getY() < p.getY()) {
			Utility.debugMsg(1, "Digging Downwards.  Extra Downwards modifier is : " + MyConfig.downModifier);
		}
		Utility.debugMsg(1, "Starting Breaking Speed : " + event.getOriginalSpeed() + " Net Final Breaking Speed:"
				+ event.getNewSpeed());

	}

}
