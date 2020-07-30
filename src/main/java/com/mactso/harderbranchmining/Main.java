//1.15.2
package com.mactso.harderbranchmining;

import net.minecraftforge.event.RegisterCommandsEvent;

import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.config.ToolManager;
import com.mactso.harderbranchmining.event.BlockBreakHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("harderbranchmining")
public class Main
{
    public static final String MODID = "harderbranchmining"; 
    
    public Main()
    {

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,MyConfig.SERVER_SPEC );
		
    }

    // Register ourselves for server and other game events we are interested in
	@SubscribeEvent 
	public void preInit (final FMLCommonSetupEvent event) {
		System.out.println("HarderBranchMining: Registering Handler");
		MinecraftForge.EVENT_BUS.register(new BlockBreakHandler ());
		
	}

    @Mod.EventBusSubscriber()
    public static class ForgeEvents
    {
		@SubscribeEvent 		
		public static void onCommandsRegistry(final RegisterCommandsEvent event) {
			System.out.println("HarderBranchMining: Registering Commands");
			HarderBranchMiningCommands.register(event.getDispatcher());			
		}

		@SubscribeEvent 
		public static void onServerStarting (FMLServerStartingEvent event) {
			System.out.println("HarderBranchMining: Initializing Toolmanager");
			ToolManager.toolInit();
		}
    }
}
