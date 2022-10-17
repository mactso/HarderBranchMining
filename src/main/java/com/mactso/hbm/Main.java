// 1.12.2 version
package com.mactso.hbm;

import com.mactso.hbm.command.Commands;
import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.event.BlockBreakHandler;
import com.mactso.hbm.manager.ExcludeListManager;
import com.mactso.hbm.manager.IncludeListManager;
import com.mactso.hbm.manager.ToolManager;
import com.mactso.hbm.network.HBMPacket;
import com.mactso.hbm.network.Manager;
import com.mactso.hbm.network.Register;
import com.mactso.hbm.util.Reference;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;


	// The value here should match an entry in the META-INF/mods.toml file
	@Mod(modid = Reference.MOD_ID, 
			name = Reference.NAME, 
			version = Reference.VERSION,
			updateJSON = "https://github.com/mactso/HarderBranchMining/raw/master/update.json")
	 public class Main
	 {
		// , serverSideOnly=true  (research this tag more)
		@Instance
		public static Main instance;
	    public static SimpleNetworkWrapper network;		

		@EventHandler
		public void preInit (FMLPreInitializationEvent event) {
			ToolManager.toolInit();
			System.out.println("HarderBranchMining: Registering Handler");
			MinecraftForge.EVENT_BUS.register(new BlockBreakHandler ());
			MinecraftForge.EVENT_BUS.register(this);		
		}
		
		@EventHandler
		public void init (FMLInitializationEvent event) {
			ExcludeListManager.excludeListInit();
			IncludeListManager.includeListInit();
			Register.initPackets();
		}

		@SubscribeEvent
		public void clientConnectionEvent (PlayerLoggedInEvent event) {
		    if (event.player instanceof EntityPlayerMP)
		    {
		    	MyConfig.serverSide = true;
		        EntityPlayerMP entity = (EntityPlayerMP) event.player;
		        System.out.println("Server dig:" + MyConfig.digSpeedModifier);
		        System.out.println("Server down:" + MyConfig.digSpeedModifier);
		        Manager.sendToClient(new HBMPacket (MyConfig.digSpeedModifier,MyConfig.downSpeedModifier), entity);
		    }			
		}
		
		@EventHandler
		public void serverLoad (FMLServerStartingEvent event) {
			System.out.println("Harder Branch Mining: Registering Commands");
			event.registerServerCommand(new Commands());
		}
	 }
