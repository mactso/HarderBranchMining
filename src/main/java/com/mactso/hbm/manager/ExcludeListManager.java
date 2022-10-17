package com.mactso.hbm.manager;

import java.util.HashSet;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ExcludeListManager {
	
	public static HashSet<Block> excludelistHashSet = new HashSet<>();

	public static void excludeListInit () {
		int i = 0;
		excludelistHashSet.clear();
		while (i < MyConfig.blocksExcludeList.length) {
			try {
				String key = MyConfig.blocksExcludeList[i];
				Block excludeBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation (key) );
				if ((excludeBlock != null) && (!(excludeBlock instanceof BlockAir))) {
					excludelistHashSet.add(excludeBlock);
				} else {
					System.out.println("HarderBranchMining: Bad Exclude list Block Config Not In Forge Registry : " + MyConfig.blocksExcludeList[i]);
				}
			}
			catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Exclude list Block Config Entry : " + MyConfig.blocksExcludeList[i]);
			}
			i++;
		}

	}

}
