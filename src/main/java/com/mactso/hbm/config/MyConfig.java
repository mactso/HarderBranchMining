// 1.12.2 version
package com.mactso.hbm.config;

import com.mactso.hbm.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Config(modid=Reference.MOD_ID)
@Mod.EventBusSubscriber
public class MyConfig
{
	@Comment( { "Exhaustion Height" } )
	@Name ("Exhaustion Height")
	@RangeDouble (min = 5, max=196)
	public static double ExhaustionHeight = 48.0;
	
	@Comment ( { "Use Proportional Exhaustion" } )
	@Name ("Use Proportional Exhaustion")
	public static boolean aBooleanProportionalExhaustion = true;
	
	@Comment ( { "Print Debugging Messages to Log" } )
	@Name ( "Print Debugging Messages to Log" )
	public static boolean aBooleanDebug = false;

    @Comment( { " ",
    	        "0 Wood Tool Exhaustion Amount" } )
    @Name( "0 Wood Tool Exhaustion Amount"  )
    @RangeDouble (min = .1, max=40.0)
	public static double ExhaustionAmountWood = 8.0;

    @Comment( { "1 Stone Tool Exhaustion Amount" } )
    @Name( "1 Stone Tool Exhaustion Amount" )
	@RangeDouble (min = .1, max=40.0)
	public static double ExhaustionAmountStone = 4.0;

    @Comment( { "2 Iron Tool Exhaustion Amount" } )
    @Name( "2 Iron Tool Exhaustion Amount" )
	@RangeDouble (min = .1, max=40.0)
  	public static double ExhaustionAmountIron = 2.0;

    @Comment( { " ",
    "3 Gold Tool Exhaustion Amount" } )
	@Name( "3 Gold Tool Exhaustion Amount"  )
	@RangeDouble (min = .1, max=40.0)
	public static double ExhaustionAmountGold = 1.5;    
    
    @Comment( { "4 Diamond Tool Exhaustion Amount" } )
    @Name( "4 Diamond Tool Exhaustion Amount" )
	@RangeDouble (min = .1, max=40.0)
	public static double ExhaustionAmountDiamond = 1.0;


	@SubscribeEvent
	public static void onModConfigEvent(OnConfigChangedEvent event)
	{
		if(event.getModID().equals(Reference.MOD_ID))
		{
			ConfigManager.sync (event.getModID(), Config.Type.INSTANCE);
			if (aBooleanDebug) {
				System.out.println("HarderBranchMiningConfig: " + ExhaustionHeight + ", " + aBooleanProportionalExhaustion);

			}
		}
	}

}
