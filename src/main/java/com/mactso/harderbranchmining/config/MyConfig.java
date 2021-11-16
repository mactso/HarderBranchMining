//1.15.2
package com.mactso.harderbranchmining.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.harderbranchmining.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig
{
	public static class Common
	{
		public final ConfigValue<String> toolsActual;
		public final ConfigValue<String> blocksWhiteListActual;
		public final IntValue	 exhaustionType;
		public final IntValue	 debugLevel;	
		public final DoubleValue digSpeedModifier;	
		public final DoubleValue downExhaustion;		
		public final BooleanValue normalOre;


		public final String defaultTools6464 = 
		  "hbm:default,hbm:default_dimension,48,10.0;"
		+ "minecraft:torch,minecraft:overworld,48,0.2;"		  
	  	+ "minecraft:wooden_pickaxe,minecraft:overworld,48,8.0;"
		+ "minecraft:stone_pickaxe,minecraft:overworld,48,4.0;"
		+ "minecraft:iron_pickaxe,minecraft:overworld,48,2.0;"
		+ "minecraft:golden_pickaxe,minecraft:overworld,48,1.0;"
		+ "minecraft:diamond_pickaxe,minecraft:overworld,48,1.2;"
		+ "minecraft:netherite_pickaxe,minecraft:overworld,48,1.1;"
		+ "minecraft:wooden_shovel,minecraft:overworld,48,8.0;"
		+ "minecraft:stone_shovel,minecraft:overworld,48,4.0;"
		+ "minecraft:iron_shovel,minecraft:overworld,48,2.0;"
		+ "minecraft:golden_shovel,minecraft:overworld,48,1.0;"
		+ "minecraft:diamond_shovel,minecraft:overworld,48,1.2;"
		+ "minecraft:netherite_shovel,minecraft:overworld,48,1.1;"
		+ "minecraft:wooden_axe,minecraft:overworld,48,2.0;"
		+ "minecraft:stone_axe,minecraft:overworld,48,1.0;"
		+ "minecraft:iron_axe,minecraft:overworld,48,2.0;"
		+ "minecraft:golden_axe,minecraft:overworld,48,1.0;"
		+ "minecraft:diamond_axe,minecraft:overworld,48,1.2;"			
		+ "minecraft:netherite_axe,minecraft:overworld,48,1.1;"			
		+ "minecraft:iron_pickaxe,minecraft:the_nether,124,2.2;"
		+ "minecraft:netherite_pickaxe,minecraft:the_nether,124,1.0;"
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
		
		public Common(ForgeConfigSpec.Builder builder) {

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
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
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
	// support for any color chattext
	public static void sendChat(Player p, String chatMessage) {
		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY));
		p.sendMessage(component, p.getUUID());
	}


	// support for any color chattext
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {
		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withColor(TextColor.fromLegacyFormat(textColor));
		p.sendMessage(component, p.getUUID());
	}
	
	// support for any color, optionally bold text.
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor, boolean boldText) {
		TextComponent component = new TextComponent (chatMessage);
		// set to Dark Green Bold
		if (boldText) {
			component.getStyle().withBold(true);
		}
		component.getStyle().withColor(TextColor.fromLegacyFormat(textColor));

		p.sendMessage(component, p.getUUID());
	}
	
	public static void bakeConfig()
	{
		aExhaustionType = COMMON.exhaustionType.get();
		aDebugLevel = COMMON.debugLevel.get();
		aDigSpeedModifier = COMMON.digSpeedModifier.get();
		aDownSpeedModifier = COMMON.downExhaustion.get();
		aNormalOreHandling = COMMON.normalOre.get();		
		aDefaultTools6464 = COMMON.toolsActual.get() ;
		aDefaultBlocksWhitelist6464 = COMMON.blocksWhiteListActual.get() ;
		System.out.println("HarderBranchMiningConfig Type:" + aExhaustionType + ", Debug Level:" + aDebugLevel);

	}
	
	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
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
		COMMON.debugLevel.set( MyConfig.aDebugLevel);
		COMMON.exhaustionType.set( MyConfig.aExhaustionType);
		COMMON.digSpeedModifier.set( MyConfig.aDigSpeedModifier);
		COMMON.downExhaustion.set( MyConfig.aDownSpeedModifier);
		COMMON.normalOre.set( MyConfig.aNormalOreHandling);
	}

}

