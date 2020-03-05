//1.15.2-2.0
package com.mactso.hbm;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.event.BlockBreakHandler;
import com.mactso.hbm.config.ToolManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("hbm")
public class Main
{
    public static final String MODID = "hbm"; 
	int i = 7;
    
    public Main()
    {

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,MyConfig.SERVER_SPEC );
		MinecraftForge.EVENT_BUS.register(this);
		
    }

    // Register ourselves for server and other game events we are interested in
	@SubscribeEvent 
	public void preInit (final FMLCommonSetupEvent event) {
		System.out.println("HarderBranchMining: Registering Handler");
		MinecraftForge.EVENT_BUS.register(new BlockBreakHandler ());
		
	}       

	// in 14.4 and later, config file loads when the server starts when the world starts.
	@SubscribeEvent 
	public void onServerStarting (FMLServerStartingEvent event) {
		
		ToolManager.toolInit();
		
	}
}
