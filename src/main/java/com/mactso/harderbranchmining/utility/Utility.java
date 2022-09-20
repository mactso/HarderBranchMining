package com.mactso.harderbranchmining.utility;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.manager.ToolManager.toolItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

public class Utility {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static double calcDepthFactor(int eventY, toolItem toolInfo) {
		double depthFactor;

		if (eventY > toolInfo.getYModifierStart()) {
			return -1;
		}
		
		int yRange = (toolInfo.getYModifierStart() - toolInfo.getYModifierStop());		
		if (yRange < 1) yRange = 1;
		
		if (eventY < toolInfo.getYModifierStop()) {
			eventY = toolInfo.getYModifierStop();
		}
		
		int altitudeFactor = (toolInfo.getYModifierStart() - eventY);

		
		depthFactor = (double) altitudeFactor / yRange ;
		return depthFactor;
	}
	
	public static String formatPercentage (double value) {
		return String.format("%5.2f%%", value * 100);
	}
	
	public static void dbgChatln(Player p, String msg, int level) {
		if (MyConfig.getDebugLevel() > level - 1) {
			sendChat(p, msg, ChatFormatting.YELLOW);
		}
	}
	
	
	public static void debugMsg(int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + ":" + dMsg);
		}

	}

	public static void debugMsg(int level, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + " (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "): " + dMsg);
		}

	}
	
	

	public static void debugMsg(int level, LivingEntity le, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + " (" 
					+ le.blockPosition().getX() + "," 
					+ le.blockPosition().getY() + ","
					+ le.blockPosition().getZ() + "): " + dMsg);
		}

	}

	public static void sendBoldChat(Player p, String chatMessage, ChatFormatting textColor) {

		MutableComponent component = Component.literal(chatMessage);
		component.setStyle(component.getStyle().withBold(true));
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendSystemMessage(component);


	}

	public static void sendChat(Player p, String chatMessage) {
		sendChat (p, chatMessage, ChatFormatting.DARK_GREEN);
	}
	
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {

		MutableComponent component = Component.literal(chatMessage);
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendSystemMessage(component);

	}

}
