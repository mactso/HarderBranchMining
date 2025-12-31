package com.mactso.harderbranchmining.common;

import com.mactso.harderbranchmining.common.managers.IgnoreBlocksListManager;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class Tests {
	
	private static int debugLimiter=0;
	
	public static boolean isSoftBlock(Player p, BlockPos pos, BlockState s) {

		double hardness = s.getDestroySpeed(p.level(), pos);
		// Block debugBlock = event.getState().getBlock();
		if (hardness > 1.0) {
			return false;
		}
		return true;
	}
	
	public static boolean isSkipBlock(String debugWorldName, Player p, Block block) {
		if (IgnoreBlocksListManager.ignoreBlocksListHashSet.contains(block)) {
			if (MyConfig.debugLevel > 1) {
				if (p instanceof ServerPlayer sp) {
					MyUtilities.sendChat(sp, debugWorldName + ", Breaking Whitelist Block at normal speed.");
				}
			}
			return true;
		}
		// f230235_a_ == ".contains()"


		if (block.defaultBlockState().is(Tags.Blocks.ORES)) {
			if ((MyConfig.debugLevel > 1) && (debugLimiter++ > 39)) {
				if (p instanceof ServerPlayer sp) {
					MyUtilities.sendChat(sp, block.toString() + " is in the Ore block tags.");
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
						MyUtilities.sendChat(sp, "Breaking Ore Block full speed with no speed adjust true.");
					}
					debugLimiter = 0;
				}
				return true;
			}
		}
		return false;
	}

}
