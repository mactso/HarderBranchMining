package com.mactso.hbm.manager;

import java.util.HashSet;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class IncludeListManager {
	
	public static HashSet<Block> includelistHashSet = new HashSet<>();

	public static void includeListInit () {
		int i = 0;
		includelistHashSet.clear();
		while (i < MyConfig.blocksIncludeList.length) {
			try {
				String key = MyConfig.blocksIncludeList[i];
				if (!key.equals("")) {
					Block includeBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation (key) );
					if ((includeBlock != null) && (!(includeBlock instanceof BlockAir))) {
						includelistHashSet.add(includeBlock);
					} else {
						System.out.println("HarderBranchMining: Bad Include list Block Config Not In Forge Registry : " + MyConfig.blocksIncludeList[i]);
					}
				}
			}
			catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Include list Block Config Entry : " + MyConfig.blocksIncludeList[i]);
			}
			i++;
		}

	}

}
