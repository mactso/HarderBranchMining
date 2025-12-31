
package com.mactso.harderbranchmining.modloader.main;

import com.mactso.harderbranchmining.common.commands.HarderBranchMiningCommands;
import com.mactso.harderbranchmining.common.managers.ToolManager;
import com.mactso.harderbranchmining.common.utilities.MyUtilities;
import com.mactso.harderbranchmining.modloader.config.MyConfig;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("harderbranchmining")
public class Main
{
    public static final String MODID = "harderbranchmining"; 
    
	public Main(FMLJavaModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(this::handleCommonSetup);
    	MyUtilities.debugMsg(0,MODID + ": Registering Mod.");
	}
	
	public void handleCommonSetup (final FMLCommonSetupEvent event) {
		// nothing happens in here any more.
	}

      @Mod.EventBusSubscriber(modid = "harderbranchmining")
    public static class ForgeEvents
    {
		@SubscribeEvent 		
		public static void onCommandsRegistry(final RegisterCommandsEvent event) {
			MyUtilities.debugMsg(0,"HarderBranchMining: Registering Commands");
			HarderBranchMiningCommands.register(event.getDispatcher());			
		}

		@SubscribeEvent 
		public static void onServerStarting (ServerStartingEvent event) {
			MyUtilities.debugMsg(0,"HarderBranchMining: Initializing Toolmanager");
			ToolManager.initTools();
		}
    }
}
