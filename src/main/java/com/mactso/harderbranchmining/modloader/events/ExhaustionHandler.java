package com.mactso.harderbranchmining.modloader.events;

import com.mactso.harderbranchmining.common.Exhaustion;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "harderbranchmining", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExhaustionHandler {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) {
			return;
		}

		if (player.isCreative()) {
			return;
		}

		if (MyConfig.getDebugLevel() > 1) {
			MyUtilities.debugMsg(2, "onBlockBreak: Applying Exhaustion To Player");
			MyUtilities.sendChat(player, "onBlockBreak: Applying Exhaustion To Player");
		}

		BlockPos pos = event.getPos();
		BlockState state = event.getState();

		
		Exhaustion.applyExhaustionForBlockBreak(player, state, pos);
	}
}
