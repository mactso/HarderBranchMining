// 1.12.2 version
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.manager.ExcludeListManager;
import com.mactso.hbm.manager.IncludeListManager;
import com.mactso.hbm.manager.ToolManager;
import com.mactso.hbm.util.Reference;
import com.mactso.hbm.utility.Utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BlockBreakHandler {

	private static int debugLimiter = 0;

	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {

		EntityPlayer player = event.getPlayer();
		
		if (player == null) {
			return;
		} else if (player.isCreative()) {
			return;
		}

		
		if (MyConfig.aExhaustionType == MyConfig.EXHAUSTION_OFF) {
			Utility.sendDbgChat(1, player, "Exhaustion is turned off");
			return;
		}
		
		// no exhaustion for soft items.
		if (isSoftBlock(event.getPos(), event.getState(), player)
				|| (isSkipBlock("calc Exhaustion", player, event.getState().getBlock()))) {
			return;
		}

		if (!IncludeListManager.includelistHashSet.isEmpty()) {
			if (!IncludeListManager.includelistHashSet.contains(event.getState().getBlock())) {
				Utility.sendDbgChat(1, player, event.getState().getBlock() + " not in non-empty Include list.");
				return;
			}
		}

		// domain:tool, dimension
		Item tempItem = player.getHeldItemMainhand().getItem();
		ToolManager.toolItem toolInfo = ToolManager
				.getToolInfo(player.getHeldItemMainhand().getItem().getRegistryName().toString(), player.dimension);

		if (!toolInfo.isExhaustionRange(event.getPos().getY())) {
			return;
		}

		Utility.sendDbgChat(1, player, toolInfo.getExhaustionAmt() + " extra exhaustion.");
		player.getFoodStats().addExhaustion((float) toolInfo.getExhaustionAmt());

	}

	private boolean isSkipBlock(String string, EntityPlayer player, Block block) {

		if (MyConfig.normalOreHandling) {
			if ((block instanceof BlockRedstoneOre) || (block instanceof BlockOre)) {
				Utility.sendDbgChat(1, player,
						"Normal Ore with Normal Ore Handling Break: " + block.toString() + ".  No exhaustion");
				return true;
			}
		}

		if (ExcludeListManager.excludelistHashSet.contains(block)) {
			Utility.sendDbgChat(1, player, "White list Block Broken: " + block.toString() + ".  No exhaustion");
			return true;
		}

		return false;

	}

	private boolean isSoftBlock(BlockPos pos, IBlockState state, EntityPlayer player) {
		double hardness = state.getBlockHardness(player.world, pos);
		if (hardness > 1.0) {
			return false;
		}
		return true;

	}

	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {

		EntityPlayer player = event.getEntityPlayer();

		if (player == null) {
			return;
		} else if (player.isCreative()) {
			return;
		}

		if (isSoftBlock(event.getPos(), event.getState(), player)
				|| (isSkipBlock("calc Speed", player, event.getState().getBlock()))) {
			return;
		}

		if (!IncludeListManager.includelistHashSet.isEmpty()) {
			if (!IncludeListManager.includelistHashSet.contains(event.getState().getBlock())) {
				Utility.sendDbgChat(1, player, event.getState().getBlock() + " not in non-empty Include list.");
				return;
			}
		}
		
		Item playerItem = player.getHeldItemMainhand().getItem();
		ToolManager.toolItem toolInfo = ToolManager.getToolInfo(playerItem.getRegistryName().toString(),
				player.dimension);

		int y = event.getPos().getY();
		if (!toolInfo.isExhaustionRange(y)) {
			return;
		}

		float newDestroySpeed = event.getOriginalSpeed();
		newDestroySpeed *= toolInfo.toolDepthModifier(y);
		newDestroySpeed /= MyConfig.digSpeedModifier;
		if (event.getPos().getY() < player.getPosition().getY()) {
			newDestroySpeed = newDestroySpeed / (float) MyConfig.downSpeedModifier;
		}

		Utility.sendDbgChat(2, player, "y: " + y + " Dig Speed " + (int) (newDestroySpeed/event.getOriginalSpeed()*100) + "% as fast.  toolDepthModifer:" + (float) toolInfo.toolDepthModifier(y)
				+ ", baseDestroy: " + event.getOriginalSpeed() + ", newdestroy:" + newDestroySpeed);
		if (newDestroySpeed > 0) {
			event.setNewSpeed(newDestroySpeed);
		}

	}

	private double calcNewDestroySpeed(float originalSpeed, int y, EntityPlayer p, ToolManager.toolItem toolInfo) {

		double newDestroySpeed = originalSpeed;

		return newDestroySpeed;
	}
}
