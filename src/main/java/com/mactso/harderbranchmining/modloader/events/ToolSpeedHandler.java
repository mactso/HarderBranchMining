package com.mactso.harderbranchmining.modloader.events;

import com.mactso.harderbranchmining.common.ToolSpeed;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
	    modid = "harderbranchmining",
	    bus = Mod.EventBusSubscriber.Bus.FORGE
	)
public class ToolSpeedHandler {

	private static int debugThrottle = 0;

	@SubscribeEvent
	public static void onBlockBreakSpeed(PlayerEvent.BreakSpeed event) {

		Player player = event.getEntity();

		if (player.isCreative()) 
			return;

		if (MyConfig.getDebugLevel() > 1) {
			if (debugThrottle % 20 == 0) {
				MyUtilities.debugMsg(2, "onBlockBreakSpeed: Modifying Dig Speed");
				if (player instanceof ServerPlayer sp)
					MyUtilities.sendChat(sp, "\nonBlockBreakSpeed: Modifying Dig Speed");
			}
			debugThrottle++;
		}

		// Safely get BlockPos (for modded stuff like artificial players)
		BlockPos pos = event.getPosition().orElse(null);
		if (pos == null)
			return;
		BlockState state = event.getState();

		ToolSpeed.modifyBreakSpeed(player, state, pos, event);
	}

}
