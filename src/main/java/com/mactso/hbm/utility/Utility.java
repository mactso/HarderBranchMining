package com.mactso.hbm.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Utility {
	private static final Logger LOGGER = LogManager.getLogger();

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

	public static void debugMsg(int level, EntityLiving le, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + " (" + le.getPosition().getX() + "," + le.getPosition().getY() + ","
					+ le.getPosition().getZ() + "): " + dMsg);
		}

	}

	public static void sendDbgChat(int level, EntityPlayer player, String msg) {
		sendDbgChat (level, player, msg, TextFormatting.GREEN);
	}
	public static void sendDbgChat(int level, EntityPlayer p, String msg, TextFormatting color) {
		if (MyConfig.getDebugLevel() > level - 1) {
			ITextComponent component = new TextComponentString(msg);
			component.getStyle().setColor(color);
			p.sendMessage(component);
		}
	}



}
