package com.mactso.hbm.config;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class whiteListManager {
	
	public static HashSet<Block> whitelistHashSet = new HashSet<>();

	public static void whitelistInit () {
		int i = 0;
		whitelistHashSet.clear();
		while (i < MyConfig.aBlocksWhiteList.length) {
			try {
				String key = MyConfig.aBlocksWhiteList[i];
				Block whitelistBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation (key) );
				if ((whitelistBlock != null) && (!(whitelistBlock instanceof BlockAir))) {
					whitelistHashSet.add(whitelistBlock);
				} else {
					System.out.println("HarderBranchMining: Bad Whitelist Block Config Not In Forge Registry : " + MyConfig.aBlocksWhiteList[i]);
				}
			}
			catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Whitelist Block Config Entry : " + MyConfig.aBlocksWhiteList[i]);
			}
			i++;
		}

	}

}
