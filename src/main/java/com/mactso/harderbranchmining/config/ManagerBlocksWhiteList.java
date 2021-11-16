//1.15.2 
package com.mactso.harderbranchmining.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;


public class ManagerBlocksWhiteList {
	
	public static HashSet<Block> whitelistHashSet = new HashSet<>();

	public static void blocksWhitelistInit () {

		int i = 0;
		List <String> dTL6464 = new ArrayList<>();
		String blocksWhitelistLine6464 = "";
		// Issue 6464 patch.
		StringTokenizer st6464 = new StringTokenizer(MyConfig.aDefaultBlocksWhitelist6464, ";");
		while (st6464.hasMoreElements()) {
			blocksWhitelistLine6464 = st6464.nextToken().trim();
			if (blocksWhitelistLine6464.isEmpty()) continue;
			dTL6464.add(blocksWhitelistLine6464);  
			i++;
		}
		
		MyConfig.aDefaultBlocksWhitelist = dTL6464.toArray(new String[i]);
		
		i = 0;
		whitelistHashSet.clear();
		while (i < MyConfig.aDefaultBlocksWhitelist.length) {
			try {
				String key = MyConfig.aDefaultBlocksWhitelist[i];
				Block whitelistBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation (key) );
				if ((whitelistBlock != null) && (!(whitelistBlock instanceof AirBlock))) {
					whitelistHashSet.add(whitelistBlock);
				} else {
					System.out.println("HarderBranchMining: Bad Whitelist Block Config Not In Forge Registry : " + MyConfig.aDefaultBlocksWhitelist[i]);
				}
			}
			catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Whitelist Block Config Entry : " + MyConfig.aDefaultBlocksWhitelist[i]);
			}
			i++;
		}

	}

}