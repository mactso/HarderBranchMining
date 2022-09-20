//1.15.2 
package com.mactso.harderbranchmining.manager;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderbranchmining.config.MyConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;


public class IgnoreBlocksListManager {
	
	private static final Logger LOGGER = LogManager.getLogger();	
	public static HashSet<Block> ignoreBlocksListHashSet = new HashSet<>();

	public static void initIgnoreBlocksList () {

		int i = 0;
		ignoreBlocksListHashSet.clear();
		while (i < MyConfig.ignoreBlocksStringArray.length) {
			try {
				String key = MyConfig.ignoreBlocksStringArray[i];
				Block ignoreBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation (key) );
				if ((ignoreBlock != null) && (!(ignoreBlock instanceof AirBlock))) {
					ignoreBlocksListHashSet.add(ignoreBlock);
				} else {
					LOGGER.warn("HarderBranchMining: Bad Ignore Block Config Not In Forge Registry : " + MyConfig.ignoreBlocksStringArray[i]);
				}
			}
			catch (Exception e) {
				LOGGER.error("HarderBranchMining: Bad Ignore Block Config Entry : " + MyConfig.ignoreBlocksStringArray[i]);
			}
			i++;
		}

	}

}