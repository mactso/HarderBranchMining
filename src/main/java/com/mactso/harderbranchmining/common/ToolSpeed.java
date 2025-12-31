package com.mactso.harderbranchmining.common;

import com.mactso.harderbranchmining.common.managers.ToolManager;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Utility class for modifying block break speed based on player tool, depth,
 * and configuration.
 * <p>
 * This class adjusts the mining speed dynamically during
 * {@link PlayerEvent.BreakSpeed} events. It accounts for:
 * <ul>
 * <li>The tool's dig modifier and exhaustion characteristics (via
 * {@link ToolManager.toolItem}).</li>
 * <li>Block type and altitude, skipping soft or excluded blocks.</li>
 * <li>Global downwards speed modifiers from {@link MyConfig}.</li>
 * <li>Client and server side differences for consistent visual speed.</li>
 * </ul>
 * <p>
 * Debug messages are provided for both chat and logs depending on
 * {@link MyConfig#debugLevel}. All calculations are safe for both vanilla and
 * modded tools/items.
 */

public class ToolSpeed {

	static int debugThrottle = 0;

	/**
	 * Adjusts a player's block breaking speed based on tool, block type, and depth.
	 * this is executed by both client and server so it will look correct. Called
	 * during {@link PlayerEvent.BreakSpeed}.
	 */
	public static void modifyBreakSpeed(Player player, BlockState state, BlockPos pos, PlayerEvent.BreakSpeed event) {

		double depthFactor = 0.0;
		String debugWorldName = "server-local ";

		if (player.level().isClientSide()) {
			debugWorldName = "client-remote ";
		}

		Item playerItem = player.getMainHandItem().getItem();

		Block targetBlock = state.getBlock();

		if (Tests.isSoftBlock(player, pos, state) || Tests.isSkipBlock(debugWorldName, player, targetBlock)) {
			return;
		}

		ResourceKey<Level> dimensionKey = player.level().dimension();
		String dimensionId = dimensionKey.identifier().toString();

		@SuppressWarnings("deprecation")
		Identifier itemKey = playerItem.builtInRegistryHolder().key().identifier();

		ToolManager.toolItem toolInfo = ToolManager.getToolInfoOrDefault(itemKey, dimensionId);

		depthFactor = MyUtilities.calcDepthFactor(toolInfo, pos.getY());

		if (depthFactor == -1.0) {
			return;
		}

		// originalSpeed already includes vanilla tool enchantments (e.g., Efficiency)
		float originalSpeed = event.getOriginalSpeed();
		int blockY = event.getPosition().get().getY(); // speed depends on depth

		double newDestroySpeed = calcNewDestroySpeed(player, originalSpeed, blockY, depthFactor, toolInfo);

		if (newDestroySpeed > 0) {
			event.setNewSpeed((float) newDestroySpeed);
		}

		if (MyConfig.debugLevel > 0) {
			if (debugThrottle % 6 == 0) // every 6th call
				logDebugInfo(player, event, targetBlock, depthFactor, dimensionId, itemKey, toolInfo);
			if (debugThrottle % 9 == 0) // every 6th call
				chatDebugInfo(player, event, depthFactor, dimensionId, itemKey, toolInfo);

			debugThrottle++;
		}
	}

	/**
	 * Calculates the new breaking speed after applying tool and depth modifiers.
	 *
	 * @param player        the player breaking the block
	 * @param originalSpeed the original breaking speed from the event
	 * @param blockY        the block Y-coordinate
	 * @param depthFactor   factor based on block depth and tool
	 * @param toolInfo      tool properties from ToolManager
	 * @return adjusted block breaking speed
	 */
	private static double calcNewDestroySpeed(Player player, float originalSpeed, int blockY, double depthFactor,
			ToolManager.toolItem toolInfo) {
		double newDestroySpeed = originalSpeed;

		if (toolInfo.getDigModifier() >= 0) {
			newDestroySpeed -= newDestroySpeed * depthFactor * toolInfo.getDigModifier();
		}

		if (blockY < player.getY() && MyConfig.downModifier >= 0) {
			newDestroySpeed -= newDestroySpeed * depthFactor * (MyConfig.downModifier * 0.01);
		}

		return newDestroySpeed;
	}

	private static void chatDebugInfo(Player p, PlayerEvent.BreakSpeed event, double depthFactor, String dimensionId,
			Identifier key, ToolManager.toolItem toolInfo) {
		MyUtilities.debugMsg(2, p, " ");
		MyUtilities.debugMsg(2, p,
				key.toString() + ":" + dimensionId + " \nExtra Exhaustion:" + toolInfo.getExhaustionAmount() + " ");
		String depthFactorF = String.format("%5.2f%%", 100 * depthFactor);
		MyUtilities.debugMsg(2, p,
				"Y altitude Info (" + toolInfo.getYModifierStart() + " -> " + event.getPosition().get().getY() + " -> "
						+ toolInfo.getYModifierStop() + ") giving " + depthFactorF + " of modifiers.");

		double toolDigMod = (toolInfo.getDigModifier()) * depthFactor;
		String toolDigModF = String.format("%5.2f%%", 100 * toolDigMod);
		MyUtilities.debugMsg(2, p, "Tool DigMod : " + toolInfo.getDigModifierAsPercent() + " Actually Slowed ("
				+ toolDigModF + ") at Y=" + event.getPosition().get().getY() + ").");

		if (event.getPosition().get().getY() < p.getY()) {
			String globalDownRawF = MyConfig.getDownModifierAsString();
			String globalDownModF = String.format("%5.2f%%", MyConfig.getDownModifier() * depthFactor);
			MyUtilities.debugMsg(2, p, " Down Modifier: " + globalDownRawF + " Slowed (" + globalDownModF + ") more.");

		}
		MyUtilities.debugMsg(2, p, "Starting Breaking Speed: " + event.getOriginalSpeed() + " Final Breaking Speed: "
				+ event.getNewSpeed());

	}

	private static void logDebugInfo(Player p, PlayerEvent.BreakSpeed event, Block b, double depthFactor,
			String dimensionId, Identifier toolkey, ToolManager.toolItem toolInfo) {
		MyUtilities.debugMsg(1, "\nMining " + BuiltInRegistries.BLOCK.getKey(b).getPath() + " with " + toolkey.getPath()
				+ " at " + " ( x=" + event.getPosition().get().getX() + ", y=" + event.getPosition().get().getY()
				+ ", z=" + event.getPosition().get().getZ() + ") " + " in the " + dimensionId + "\n" + toolkey.getPath()
				+ " slows from (" + toolInfo.getYModifierStart() + "y to slowest speed below "
				+ toolInfo.getYModifierStop() + "y ) " + "\nExtra Exhaustion applied for " + toolkey.getPath()
				+ " at this depth is :" + toolInfo.getExhaustionAmount());

		MyUtilities.debugMsg(1, "Tool Basic Digging Modifier: -" + toolInfo.getDigModifierAsPercent() + ".");
		if (event.getPosition().get().getY() < p.getY()) {
			MyUtilities.debugMsg(1, "Digging Downwards.  Extra Downwards modifier is : " + MyConfig.downModifier);
		}
		MyUtilities.debugMsg(1, "Starting Breaking Speed : " + event.getOriginalSpeed() + " Net Final Breaking Speed:"
				+ event.getNewSpeed());

	}

}
