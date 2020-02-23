// 1.12.2 version
package com.mactso.hbm;

import com.mactso.hbm.config.toolManager;
import com.mactso.hbm.event.BlockBreakHandler;
import com.mactso.hbm.util.Reference;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


	// The value here should match an entry in the META-INF/mods.toml file
	@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
	 public class Main
	 {
		// , serverSideOnly=true  (research this tag more)
		@Instance
		public static Main instance;

		@EventHandler
		public void preInit (FMLPreInitializationEvent event) {
			toolManager.toolInit();
			System.out.println("HarderBranchMining: Registering Handler");
			MinecraftForge.EVENT_BUS.register(new BlockBreakHandler ());
			
		}

	 }
