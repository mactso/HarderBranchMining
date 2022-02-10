//1.15.2-2.0.0.3
package com.mactso.harderbranchmining.event;

import com.mactso.harderbranchmining.config.ManagerBlocksWhiteList;
import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.config.ToolManager;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class BlockBreakHandler {

	private static int debugLimiter = 0;

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

		double depthBasedExhaustionFactor = 0.0;
		double tempExhaustionAmount = 0;

		Player p = event.getPlayer();
		// Item item = p.getHeldItemMainhand().getItem();

		// no exhaustion for soft items.

		float hardness = event.getState().getDestroySpeed(event.getWorld(), event.getPos());
		if (1 >= event.getState().getDestroySpeed(event.getWorld(), event.getPos())) {
			if (MyConfig.debugLevel > 1) {
				MyConfig.sendChat(p, "Block Broken! Soft Block.  No Exhaustion.");
			}
			return;
		}




		// World debugWorldValue = (World) event.getWorld();
		// ItemStack debugItemStack = event.getPlayer().getHeldItemMainhand();
		Item tempItem = event.getPlayer().getMainHandItem().getItem();

		// domain:tool:dimension
		DimensionType dimensionType = p.level.dimensionType();
		ResourceKey<Level> dimensionKey = p.level.dimension();
		String dimensionId = dimensionKey.location().toString();
		ToolManager.toolItem toolInfo = ToolManager.getToolInfo(tempItem.getRegistryName().toString(), dimensionId);
		
		if (event.getPos().getY() > toolInfo.getYModifierStart()) {
			return;
		}
		
		if (isSoftBlock(event.getPos(), event.getState(), p) || (isSkipBlock("calc Exhaustion", p, event.getState().getBlock()))) {
			return;
		}
		
		
		depthBasedExhaustionFactor = toolInfo.getYModifierStart() - event.getPos().getY();
		double depthFactor = getDepthFactor(event.getPos().getY(), "exhaustion", toolInfo.getYModifierStart());
		int debug5 = 5;
		if (toolInfo.getYModifierStart() < event.getPos().getY()) {
			return;
		} else {
			if (MyConfig.exhaustionType == MyConfig.EXHAUSTION_DEPTH) {
				depthBasedExhaustionFactor = depthFactor;
			} else { // EXHAUSTION_FIXED
				depthBasedExhaustionFactor = 1.0;
			}
		}

		tempExhaustionAmount = toolInfo.getExhaustionAmt() * depthBasedExhaustionFactor;

		event.getPlayer().getFoodData().addExhaustion((float) tempExhaustionAmount);

		if (MyConfig.debugLevel > 0) {
			System.out
					.println("Block Broken! Player:" + p.getName().getString().toString() + ", Dimension:" + dimensionId
							+ ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);
			if (MyConfig.debugLevel > 1) {

				MyConfig.sendChat(p,
						"Block Broken! \n With " + tempItem.getRegistryName().toString() + " by Player:"
								+ p.getGameProfile().getName() + "\n Dimension   :" + dimensionId + "\n Depth       :"
								+ event.getPos().getY() + "\n Exhaustion:"
								+ Math.round(tempExhaustionAmount * 1000.0) / 1000.0);
			}
		}
	}

	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
		double depthFactor = 0.0;
		String debugWorldName = "server-local ";

		if (event.getPlayer() == null) {
			return;
		} else if (event.getPlayer().isCreative()) {
			return;
		}

		Player p = event.getPlayer();
		if (p.level.isClientSide()) {
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
		if (isSoftBlock(event.getPos(), event.getState(), p) || (isSkipBlock(debugWorldName, p, block))) {
			return;
		}

		// key = moddomain:tool,dimension

		ResourceKey<Level> dimensionKey = p.level.dimension();
		String dimensionId = dimensionKey.location().toString();

		ToolManager.toolItem toolInfo = ToolManager.getToolInfo(playerItem.getRegistryName().toString(), dimensionId);

		depthFactor = getDepthFactor(event.getPos().getY(), dimensionId, toolInfo.getYModifierStart());
//		depthFactor = toolInfo.getYModifierStart() - event.getPos().getY();
		int y = 4;
		if (depthFactor == -1.0) {
			return;
		}

		double newDestroySpeed = event.getOriginalSpeed();

		double speedReductionAmount = (newDestroySpeed  * (1.0 - toolInfo.getDigModifier()) * depthFactor);
		newDestroySpeed -= speedReductionAmount;

		speedReductionAmount = (newDestroySpeed  * (1.0 - MyConfig.digModifier) * depthFactor);
		newDestroySpeed -= speedReductionAmount;

		if (event.getPos().getY() < p.getY()) {
			speedReductionAmount = (newDestroySpeed * (1.0 - MyConfig.downModifier) * depthFactor);
			newDestroySpeed -= speedReductionAmount;
		}
		y = 3;
		if (newDestroySpeed > 0) {
			event.setNewSpeed((float) newDestroySpeed);
		}

		if (MyConfig.debugLevel > 0) {
			if (debugLimiter++ > 5) { // debugLimiter to avoid spamming chat window
				System.out.println("dbgL:" + MyConfig.debugLevel + " exT:" + MyConfig.exhaustionType + " DSM:"
						+ MyConfig.digModifier);
				System.out.println(
						"Breaking Block Speed ! depthSpeedFactor:" + (float) (depthFactor * 100) + "%");
				System.out.println(
						"Tool Dig Speed Modifier:" + (float) (toolInfo.getDigModifier() * 100) + "%");
				System.out.println("Breaking Block Speed ! Configured digSpeedModifer: "
						+ (float) (MyConfig.digModifier * 100) + "%");
				System.out.println("Breaking Block Speed ! Original Speed: " + event.getOriginalSpeed()
						+ " newSpeedSet:" + (event.getNewSpeed()) + " DigSpeedMod:" + MyConfig.digModifier + ".");
				if (MyConfig.debugLevel > 1 && p.level.isClientSide()) {
					String msg2 = "";
					if (event.getPos().getY() < p.getY()) {
						msg2 = "\nExtra Downward Speed Modifier  .: " + MyConfig.downModifier;
					}
					String msg = "\nClientSide " + debugWorldName
							+ " :  Breaking Block Speed ! \n Default Minecraft Digging Speed  : " + event.getOriginalSpeed()  
							+ "\n Standard Digging Speed Modifier .:" + MyConfig.digModifier *100 + "%"
							+ "\nTool Dig Speed Modifier: " + (float) (toolInfo.getDigModifier() * 100) + "%"
							+ msg2 
							+ "\n Final Modified Breaking Speed           .: " + (event.getNewSpeed()) + "\n Player Y = "
							+ p.getY() + " Block Y = " + event.getPos().getY() + "";
					MyConfig.sendChat(p, msg);
					if (event.getPos().getY() < p.getY()) {
						msg = " Extra Downward Speed Modifier  .: " + MyConfig.downModifier;
						MyConfig.sendChat(p, msg, ChatFormatting.YELLOW);
					}
				}
				debugLimiter = 0;
			}

		}
	}

	private boolean isSoftBlock(BlockPos pos, BlockState s, Player p) {

		double hardness = s.getDestroySpeed(p.level, pos);
		// Block debugBlock = event.getState().getBlock();
		if (hardness > 1.0) {
			return false;
		}
		return true;
	}

	private boolean isSkipBlock(String debugWorldName, Player p, Block block) {
		if (ManagerBlocksWhiteList.whitelistHashSet.contains(block)) {
			if (MyConfig.debugLevel > 1) {
				MyConfig.sendChat(p, debugWorldName + ", Breaking Whitelist Block at normal speed.");
			}
			return true;
		}
		// f230235_a_ == ".contains()"
		if ((Tags.Blocks.ORES).contains(block)) {
			if ((MyConfig.debugLevel > 1) && (debugLimiter++ > 39)) {
				MyConfig.sendChat(p, block.getDescriptionId().toString() + " is in the Ore block tags.");
				debugLimiter = 0;
			}
			return true;
		}
		// no exhaustion for ore block items.
		if ((block instanceof RedStoneOreBlock) || (block instanceof OreBlock)) {
			if (MyConfig.normalOreHandling) {
				if ((MyConfig.debugLevel > 1) && (debugLimiter++ > 39)) {
					MyConfig.sendChat(p, "Breaking Ore Block full speed with no speed adjust true.");
					debugLimiter = 0;
				}
				return true;
			}
		}
		return false;
	}

	private double getDepthFactor(int eventY, String dimensionId, int YModifierStart) {
		double depthFactor;

		if (eventY > YModifierStart) {
			return -1;
		}
		
		int yRange = (YModifierStart - MyConfig.minDepthLimit);		
		if (yRange < 1) yRange = 1;
		
		if (eventY < MyConfig.minDepthLimit) {
			eventY = MyConfig.minDepthLimit;
		}
		
		int altitudeFactor = (YModifierStart - eventY);

		
		depthFactor = (double) altitudeFactor / yRange ;
		return depthFactor;
	}

}
