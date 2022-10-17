// 1.12.2 version
package com.mactso.hbm.config;

import com.mactso.hbm.manager.ExcludeListManager;
import com.mactso.hbm.manager.IncludeListManager;
import com.mactso.hbm.manager.ToolManager;
import com.mactso.hbm.util.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Config(modid=Reference.MOD_ID)
@Mod.EventBusSubscriber
public class MyConfig
{
	@Ignore
	public static final int EXHAUSTION_OFF = 0;
	@Ignore
	public static final int EXHAUSTION_DEPTH = 1;	
	@Ignore
	public static final int EXHAUSTION_FIXED = 2;

	@Ignore
	public static double serverDigSpeed = 1.0;

	@Ignore
	public static double serverDownSpeed = 1.01;
	
	@Ignore
	public static boolean serverSide = false;
	
	@Comment ( { "Exhaustion Type : 0=No Hunger, 1=Depth Proportional Hunger & Speed, 2=Fixed Hunger & Speed" } )
	@Name ("Exhaustion Type 0,1,2")
	@RangeInt (min=0, max=2)
	public static int aExhaustionType = 1;

	@Comment ( { "Depth Dig Speed Modifier" } )
	@Name ("Dig   1 to 11.0 : 'No Effect' to 'Slow'")
	@RangeDouble (min=1.0, max=11.0)
	public static double digSpeedModifier = 1.09;

	public static int getDebugLevel() {
		return debugLevel;
	}

	public static void setDebugLevel(int debugLevel) {
		if ((debugLevel < 0) || (debugLevel > 2)) {
			debugLevel = 0;
		}
		MyConfig.debugLevel = debugLevel;
	}

	@Comment ( { "Down Speed Modifier" } )
	@Name ("Down 1 to 11.0 : 'No Effect' to 'Slow'")
	@RangeDouble (min=1.0, max=11.0)
	public static double downSpeedModifier = 1.03;	

	@Comment ( { "Normal Ore Speed?" } )
	@Name ("True: Normal, False: Exhausting & Slow Ore")
	public static boolean normalOreHandling = true;		
	
	@Comment ( { "Print Debugging Messages to Log" } )
	@Name ( "Log Debugging: 0=minimal, 1= log, 2 = log + chat" )
	@RangeInt (min=0, max=2)
	public static int debugLevel = 0;

	@Comment ( { "Tool Values: mod:tool, Dimension #, ExhaustionMaxY(5-255), Exhaustion Amount(0-40)" } )
	@Name ( "Tool Values: mod:tool, Dim, Height, Exhaustion" )
	public static String [] defaultTools= 
	{	    "hbm:default,0,48,10.0",
			"Minecraft:torch,0,48,0.01",
			"minecraft:wooden_pickaxe,0,48,8.0",
			"minecraft:stone_pickaxe,0,48,4.0",
			"minecraft:iron_pickaxe,0,48,2.0",
			"minecraft:gold_pickaxe,0,48,1.5",
			"minecraft:diamond_pickaxe,0,48,1.2",
			"minecraft:wooden_shovel,0,48,8.0",
			"minecraft:stone_shovel,0,48,4.0",
			"minecraft:iron_shovel,0,48,2.0",
			"minecraft:gold_shovel,0,48,1.5",
			"minecraft:diamond_shovel,0,48,1.2",
			"minecraft:wooden_axe,0,48,2.0",
			"minecraft:stone_axe,0,48,1.0",
			"minecraft:iron_axe,0,48,0.5",
			"minecraft:gold_axe,0,48,0.3",
			"minecraft:diamond_axe,0,48,1.25",			
			"minecraft:iron_pickaxe,-1,124,2.2",
	        "minecraft:diamond_pickaxe,-1,124,1.3"
	};
 
	@Comment ( { "Exclude Blocks: Mod:Block" } )
	@Name ( "Exclude Blocks : Mod: Block" )
	public static String [] blocksExcludeList= 
	{	    "ore_stone_variants:coal_ore",
			"ore_stone_variants:iron_ore",
			"ore_stone_variants:gold_ore",
			"ore_stone_variants:diamond_ore",
			"ore_stone_variants:lapis_ore",
			"ore_stone_variants:redstone_ore",
			"rockcandy:candy_ore",
			"minecraft:planks",
			"minecraft:fence"
	};	

	@Comment ( { "Include Blocks: Mod:Block.  If populated, ONLY these blocks are slowed." } )
	@Name ( "include Blocks : Mod: Block" )
	public static String [] blocksIncludeList= 
	{	    "", 
			"",
			""
	};	

	
	@SubscribeEvent
	public static void onModConfigEvent(OnConfigChangedEvent event)
	{

		if(event.getModID().equals(Reference.MOD_ID))
		{
			ConfigManager.sync (event.getModID(), Config.Type.INSTANCE);
			ToolManager.toolInit();
			ExcludeListManager.excludeListInit();
			IncludeListManager.includeListInit();
			
			if (!serverSide) {
				if (debugLevel>0) {
					System.out.println("HarderBranchMining ("+ serverSide+") Configuration Change "+digSpeedModifier+"reset to=" + serverDigSpeed);
					System.out.println("HarderBranchMining ("+ serverSide+") Configuration Change "+downSpeedModifier+"reset to=" + serverDownSpeed);
				}				
				digSpeedModifier = serverDigSpeed;
				downSpeedModifier = serverDownSpeed;
			}
		}
	}

}
