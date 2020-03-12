//1.15.2-2.0
package com.mactso.hbm.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.hbm.Main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig
{
	public static final Server SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;
	static
	{
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	public static int    aExhaustionType;
	public static int    aDebugLevel;
	public static double    aDigSpeedModifier;
	public static String[] defaultTools;
	public static String defaultTools6464;


	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.SERVER_SPEC)
		{
			bakeConfig();
			ToolManager.toolInit();
		}
	}

	public static void pushValues() {
		System.out.println("dbgL:"+MyConfig.aDebugLevel
						 +" exT:"+MyConfig.aExhaustionType
						 +" DSM:" + MyConfig.aDigSpeedModifier);
		SERVER.debugLevel.set( MyConfig.aDebugLevel);
		SERVER.exhaustionType.set( MyConfig.aExhaustionType);
		SERVER.aDigSpeedModifier.set( MyConfig.aDigSpeedModifier);

	}
	
	public static void bakeConfig()
	{
		aExhaustionType = SERVER.exhaustionType.get();
		aDebugLevel = SERVER.debugLevel.get();
		aDigSpeedModifier = SERVER.aDigSpeedModifier.get();
//		defaultTools = (String[]) SERVER.defaultTools.get().toArray();
//		String[] str = map1.keySet().toArray(new String[map1.size()]);	
		defaultTools6464 = SERVER.defaultToolsActual.get() ;
		defaultTools = SERVER.defaultTools.get().toArray(new String[SERVER.defaultTools.get().size()]);
		System.out.println("HarderBranchMiningConfig Type:" + aExhaustionType + ", Debug Level:" + aDebugLevel);

	}

	public static class Server
	{
		public final IntValue	 exhaustionType;
		public final IntValue	 debugLevel;
		public final DoubleValue aDigSpeedModifier;
		public final ConfigValue<String> defaultToolsActual;		
		public final String defaultTools6464 = 
		  "hbm:default,0,48,10;\n\r"
	  	+ "minecraft:wooden_pickaxe,0,48,8.0;\n\r"
		+ "minecraft:stone_pickaxe,0,48,4.0;\n\r"
		+ "minecraft:iron_pickaxe,0,48,2.0;\n\r"
		+ "minecraft:golden_pickaxe,0,48,1.5;\n\r"
		+ "minecraft:diamond_pickaxe,0,48,1.0;\n\r"
		+ "minecraft:wooden_shovel,0,48,8.0;\n\r"
		+ "minecraft:stone_shovel,0,48,4.0;\n\r"
		+ "minecraft:iron_shovel,0,48,2.0;\n\r"
		+ "minecraft:golden_shovel,0,48,1.5;\n\r"
		+ "minecraft:diamond_shovel,0,48,1.0;\n\r"
		+ "minecraft:wooden_axe,0,48,2.0;\n\r"
		+ "minecraft:stone_axe,0,48,1.0;\n\r"
		+ "minecraft:iron_axe,0,48,0.5;\n\r"
		+ "minecraft:golden_axe,0,48,0.3;\n\r"
		+ "minecraft:diamond_axe,0,48,0.25;\n\r"			
		+ "minecraft:iron_pickaxe,-1,124,2.2;\n\r"
		+ "minecraft:diamond_pickaxe,-1,124,1.2;\n\r";
		
		public final ConfigValue<List<String>> defaultTools;
		// "Tool Values: mod:tool, Dim, Height, Exhaustion"
		public static List<String> defaultToolsArray= Arrays.asList(
				    "hbm:default,0,48,10",
				    "minecraft:wooden_pickaxe,0,48,8.0",
					"minecraft:stone_pickaxe,0,48,4.0",
					"minecraft:iron_pickaxe,0,48,2.0",
					"minecraft:golden_pickaxe,0,48,1.5",
					"minecraft:diamond_pickaxe,0,48,1.0",
					"minecraft:wooden_shovel,0,48,8.0",
					"minecraft:stone_shovel,0,48,4.0",
					"minecraft:iron_shovel,0,48,2.0",
					"minecraft:golden_shovel,0,48,1.5",
					"minecraft:diamond_shovel,0,48,1.0",
					"minecraft:wooden_axe,0,48,2.0",
					"minecraft:stone_axe,0,48,1.0",
					"minecraft:iron_axe,0,48,0.5",
					"minecraft:golden_axe,0,48,0.3",
					"minecraft:diamond_axe,0,48,0.25",			
					"minecraft:iron_pickaxe,-1,124,2.2",
			        "minecraft:diamond_pickaxe,-1,124,1.2"
			);

		public Server(ForgeConfigSpec.Builder builder) {

			builder.push("Exhaustion Control Values");
			exhaustionType= builder
					.comment("Exhaustion Type: 0 = Fixed, 1=Proportional")
					.translation(Main.MODID + ".config." + "exhaustionType")
					.defineInRange("exhaustionType", () -> 1, 0, 1);

			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);
			
			aDigSpeedModifier = builder
					.comment("Digging Speed Modifer: (none) 1.0 to (max) 16.0")
					.translation(Main.MODID + ".config." + "aDigSpeedModifier")
					.defineInRange("aDigSpeedModifier", () -> 1.09, 1.0, 16.0);
			builder.pop();
			
			builder.push ("Tool Values 6464");
			defaultToolsActual = builder
					.comment("Tool String 6464")
					.translation(Main.MODID + ".config" + "defaultToolsActual")
					.define("defaultToolsActual", defaultTools6464);
			builder.pop();
			
			builder.push("Tool Values");
			defaultTools = builder
					.comment("Tool String")
					.translation(Main.MODID + ".config" + "defaultTools")
					.define("defaultTools", defaultToolsArray );					
			builder.pop();



		}
	}
	
}

