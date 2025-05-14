
package com.mactso.harderbranchmining;

import com.mactso.harderbranchmining.commands.HarderBranchMiningCommands;
import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.event.BlockBreakHandler;
import com.mactso.harderbranchmining.manager.ToolManager;
import com.mactso.harderbranchmining.utility.Utility;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
		context.getModEventBus().register(this);
		context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	}

    // Register ourselves for server and other game events we are interested in
	@SubscribeEvent 
	public void preInit (final FMLCommonSetupEvent event) {
		Utility.debugMsg(0,"HarderBranchMining: Registering Handler");
		MinecraftForge.EVENT_BUS.register(new BlockBreakHandler ());
		
	}

    @Mod.EventBusSubscriber()
    public static class ForgeEvents
    {
		@SubscribeEvent 		
		public static void onCommandsRegistry(final RegisterCommandsEvent event) {
			Utility.debugMsg(0,"HarderBranchMining: Registering Commands");
			HarderBranchMiningCommands.register(event.getDispatcher());			
		}

		@SubscribeEvent 
		public static void onServerStarting (ServerStartingEvent event) {
			Utility.debugMsg(0,"HarderBranchMining: Initializing Toolmanager");
			ToolManager.initTools();
		}
    }
}
