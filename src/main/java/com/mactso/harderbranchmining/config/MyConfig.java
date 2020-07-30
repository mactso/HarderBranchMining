//1.15.2
package com.mactso.harderbranchmining.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.harderbranchmining.Main;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig
{
	public static class Server
	{
		public final ConfigValue<String> toolsActual;
		public final ConfigValue<String> blocksWhiteListActual;
		public final IntValue	 exhaustionType;
		public final IntValue	 debugLevel;	
		public final DoubleValue digSpeedModifier;	
		public final DoubleValue downExhaustion;		
		public final BooleanValue normalOre;


		public final String defaultTools6464 = 
		  "hbm:default,0,48,10.0;"
		+ "minecraft:torch,0,48,0.2;"		  
	  	+ "minecraft:wooden_pickaxe,0,48,8.0;"
		+ "minecraft:stone_pickaxe,0,48,4.0;"
		+ "minecraft:iron_pickaxe,0,48,2.0;"
		+ "minecraft:golden_pickaxe,0,48,1.5;"
		+ "minecraft:diamond_pickaxe,0,48,1.0;"
		+ "minecraft:wooden_shovel,0,48,8.0;"
		+ "minecraft:stone_shovel,0,48,4.0;"
		+ "minecraft:iron_shovel,0,48,2.0;"
		+ "minecraft:golden_shovel,0,48,1.5;"
		+ "minecraft:diamond_shovel,0,48,1.0;"
		+ "minecraft:wooden_axe,0,48,2.0;"
		+ "minecraft:stone_axe,0,48,1.0;"
		+ "minecraft:iron_axe,0,48,0.5;"
		+ "minecraft:golden_axe,0,48,0.3;"
		+ "minecraft:diamond_axe,0,48,0.25;"			
		+ "minecraft:iron_pickaxe,-1,124,2.2;"
		+ "minecraft:diamond_pickaxe,-1,124,1.2;"
		;		
		public final String defaultBlocksWhiteList6464 = 
				    "ore_stone_variants:coal_ore;"+
					"ore_stone_variants:iron_ore;"+
					"ore_stone_variants:gold_ore;"+
					"ore_stone_variants:diamond_ore;"+
					"ore_stone_variants:lapis_ore;"+
					"ore_stone_variants:redstone_ore;"+
					"rockcandy:candy_ore;"+
					"minecraft:oak_planks;"+
					"minecraft:oak_fence;"
			;	
		
		public Server(ForgeConfigSpec.Builder builder) {

			builder.push("Exhaustion Control Values");
			exhaustionType= builder
					.comment("Exhaustion Type: 0 = None-Speedonly, 1=Proportional, 2=Fixed")
					.translation(Main.MODID + ".config." + "exhaustionType")
					.defineInRange("exhaustionType", () -> 1, 0, 2);

			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);
			
			digSpeedModifier = builder
					.comment("Digging Speed Modifer: (none) 1.0 to (max) 32.0")
					.translation(Main.MODID + ".config." + "aDigSpeedModifier")
					.defineInRange("aDigSpeedModifier", () -> 1.09, 1.0, 32.0);
			
			downExhaustion = builder
					.comment("Down Speed Modifer: (none) 1.0 to (max) 32.0")
					.translation(Main.MODID + ".config." + "aDownExhaustion")
					.defineInRange("aDownExhaustion", () -> 1.03, 1.0, 32.0);

			normalOre = builder
					.comment("Normal Ore Behavior: true")
					.translation(Main.MODID + ".config." + "aNormalOre")
					.define ("aNormalOre", () -> true);
			builder.pop();
			
			builder.push ("Tool Values 6464");
			toolsActual = builder
					.comment("Tool String 6464")
					.translation(Main.MODID + ".config" + "defaultToolsActual")
					.define("defaultToolsActual", defaultTools6464);
			builder.pop();

			builder.push ("Blocks White List Values 6464");
			blocksWhiteListActual = builder
					.comment("Blocks Whitelist String 6464")
					.translation(Main.MODID + ".config" + "defaultBlocksWhiteListActual")
					.define("defaultBlocksWhiteListActual", defaultBlocksWhiteList6464);
			builder.pop();

		}

	}
	
	public static final Server SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static
	{
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}
	public static int       aExhaustionType;
	public static int       aDebugLevel;
	public static double    aDownSpeedModifier;
	public static double    aDigSpeedModifier;
	public static boolean   aNormalOreHandling;
	public static String[]  aDefaultTools;
	public static String    aDefaultTools6464;
	public static String[]  aDefaultBlocksWhitelist;
	public static String    aDefaultBlocksWhitelist6464;
	public static final Boolean BOLD = true;
	public static final int EXHAUSTION_OFF   = 0;
	public static final int EXHAUSTION_DEPTH = 1;
	public static final int EXHAUSTION_FIXED = 2;

    // for this mod- default color is green.
	public static void sendChat(PlayerEntity p, String chatMessage) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		// set to Dark Green Bold
		Style chatStyle = Style.field_240709_b_.func_240712_a_(TextFormatting.DARK_GREEN);
		p.sendMessage(component.func_230530_a_(chatStyle) , p.getUniqueID());
	}

	// support for any color chattext
	public static void sendChat(PlayerEntity p, String chatMessage, TextFormatting textColor) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		Style chatStyle = Style.field_240709_b_.func_240712_a_(textColor);
		p.sendMessage(component.func_230530_a_(chatStyle) , p.getUniqueID());
	}
	
	// support for any color, optionally bold text.
	public static void sendChat(PlayerEntity p, String chatMessage, TextFormatting textColor, boolean boldText) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		// set to Dark Green Bold
		Style chatStyle = Style.field_240709_b_.func_240712_a_(textColor).func_240713_a_(boldText);
		p.sendMessage(component.func_230530_a_(chatStyle) , p.getUniqueID());
	}
	
	public static void bakeConfig()
	{
		aExhaustionType = SERVER.exhaustionType.get();
		aDebugLevel = SERVER.debugLevel.get();
		aDigSpeedModifier = SERVER.digSpeedModifier.get();
		aDownSpeedModifier = SERVER.downExhaustion.get();
		aNormalOreHandling = SERVER.normalOre.get();		
		aDefaultTools6464 = SERVER.toolsActual.get() ;
		aDefaultBlocksWhitelist6464 = SERVER.blocksWhiteListActual.get() ;
		System.out.println("HarderBranchMiningConfig Type:" + aExhaustionType + ", Debug Level:" + aDebugLevel);

	}
	
	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.SERVER_SPEC)
		{
			bakeConfig();
			ToolManager.toolInit();
			ManagerBlocksWhiteList.blocksWhitelistInit();
		}
	}

	public static void pushValues() {
		System.out.println("dbgL:"+MyConfig.aDebugLevel
						 +" exhT:"+MyConfig.aExhaustionType
						 +" DigSM:" + MyConfig.aDigSpeedModifier
						 +" DwnSM:" + MyConfig.aDownSpeedModifier
						 +" NmOre:" + MyConfig.aNormalOreHandling );
		SERVER.debugLevel.set( MyConfig.aDebugLevel);
		SERVER.exhaustionType.set( MyConfig.aExhaustionType);
		SERVER.digSpeedModifier.set( MyConfig.aDigSpeedModifier);
		SERVER.downExhaustion.set( MyConfig.aDownSpeedModifier);
		SERVER.normalOre.set( MyConfig.aNormalOreHandling);
	}

}

