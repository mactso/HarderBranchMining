//1.15.2
package com.mactso.harderbranchmining.config;

import java.util.Arrays;
import java.util.List;

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
		public final ConfigValue<List<? extends String>> toolsList;
		public final ConfigValue<List<? extends String>> ignoreBlocksList;

		public final IntValue	 exhaustionType;
		public final IntValue	 debugLevel;	
		public final IntValue    downModifier;		
		public final BooleanValue normalOre;
		
		public Common(ForgeConfigSpec.Builder builder) {
			List<String> defaultIgnoreBlocksList = Arrays.asList(
				    "ore_stone_variants:coal_ore",
					"ore_stone_variants:iron_ore",
					"ore_stone_variants:gold_ore",
					"ore_stone_variants:diamond_ore",
					"ore_stone_variants:lapis_ore",
					"ore_stone_variants:redstone_ore",
					"rockcandy:candy_ore",
					"minecraft:oak_planks",
					"minecraft:oak_fence"
			);	
			List<String> defaultToolsList = Arrays.asList(
					  "hbm:default1,hbm:default_dimension1,48,0,25,50"
								, "minecraft:torch,minecraft:overworld,48,-32,0,100"
							  	, "vanillaplustools:diamond_hammer,minecraft:overworld,48,-32,4,45"
							  	, "vanillaplustools:*,minecraft:overworld,48,-32,50,40"
							  	, "minecraft:wooden_pickaxe,minecraft:overworld,60,48,90,85"
								, "minecraft:stone_pickaxe,minecraft:overworld,55,32,50,75"
								, "minecraft:iron_pickaxe,minecraft:overworld,48,10,25,65"
								, "minecraft:golden_pickaxe,minecraft:overworld,32,-32,10,55"
								, "minecraft:diamond_pickaxe,minecraft:overworld,32,-32,4,45"
								, "minecraft:netherite_pickaxe,minecraft:overworld,16,-60,2,35"
								, "minecraft:wooden_shovel,minecraft:overworld,60,50,90,85"
								, "minecraft:stone_shovel,minecraft:overworld,55,30,50,75"
								, "minecraft:iron_shovel,minecraft:overworld,48,0,25,65"
								, "minecraft:golden_shovel,minecraft:overworld,48,0,10,55"
								, "minecraft:diamond_shovel,minecraft:overworld,48,-32,4,45"
								, "minecraft:netherite_shovel,minecraft:overworld,48,-48,2,35"
								, "minecraft:wooden_axe,minecraft:overworld,60,40,90,85"
								, "minecraft:stone_axe,minecraft:overworld,60,32,50,75"
								, "minecraft:iron_axe,minecraft:overworld,48,10,25,65"
								, "minecraft:golden_axe,minecraft:overworld,32,-32,10,35"
								, "minecraft:diamond_axe,minecraft:overworld,48,-32,4,35"			
								, "minecraft:netherite_axe,minecraft:overworld,32,-32,2,35"			
							  	, "minecraft:wooden_pickaxe,minecraft:the_nether,124,32,90,85"
								, "minecraft:stone_pickaxe,minecraft:the_nether,124,24,50,75"
								, "minecraft:iron_pickaxe,minecraft:the_nether,124,16,25,65"
								, "minecraft:golden_pickaxe,minecraft:the_nether,124,8,10,5"
								, "minecraft:diamond_pickaxe,minecraft:the_nether,124,4,4,25"
								, "minecraft:netherite_pickaxe,minecraft:the_nether,124,4,2,5"
					);
			
			builder.push("Exhaustion Control Values");
			exhaustionType= builder
					.comment("Exhaustion Type: 0 = None-Speedonly, 1=Proportional, 2=Fixed")
					.translation(Main.MODID + ".config." + "exhaustionType")
					.defineInRange("exhaustionType", () -> 1, 0, 2);

			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);
	
			downModifier = builder
					.comment("Downwards Speed & Exhaustion Modifer: Reduce downard digging to 80% of normal.")
					.translation(Main.MODID + ".config." + "downModifier")
					.defineInRange("downModifier", () -> 20, 0, 100);

			normalOre = builder
					.comment("Normal Ore Behavior: true")
					.translation(Main.MODID + ".config." + "normalOre")
					.define ("normalOre", () -> true);
			builder.pop();
			
			builder.push ("Tool Config Values : tool,dimension , upperYlimit, lowerYlimit, exhaustion, pct slower;");

			toolsList = builder
					.comment("list of tool configurations")
					.translation(Main.MODID + ".config" + "toolsList")
					.defineList("toolsList", defaultToolsList, Common::isString);
			
			builder.pop();

			
			
			
			builder.push ("Blocks Ignore List ");
			ignoreBlocksList = builder
					.comment("Blocks Ignored by Harder Branch Mining .")
					.translation(Main.MODID + ".config" + "ignoreBlocksList")
					.defineList("ignoreBlocksList", defaultIgnoreBlocksList, Common::isString);
			builder.pop();

		}
		
		public static boolean isString(Object o)
		{
			return (o instanceof String);
		}

	}
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	public static String exhaustionTypeDescriptions[] = new String []{"None","Proportional","Fixed"};
	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	public static int       exhaustionType;
	public static int       debugLevel;
	public static int getDebugLevel() {
		return debugLevel;
	}


	public static void setDebugLevel(int debugLevel) {
		MyConfig.debugLevel = debugLevel;
	}


	public static double getDownModifier() {
		return (double) (downModifier * 0.01);
	}

	public static String getDownModifierAsString () {
		return String.format("%5.2f%%", getDownModifier() );
	}

	public static void setDownModifier(int downModifier) {
		MyConfig.downModifier = downModifier;
	}

	public static int getExhaustionType() {
		return exhaustionType;
	}

	public static String getExhaustionTypeAsString() {
		return exhaustionTypeDescriptions [exhaustionType];
	}
	
	public static int       downModifier;
	public static boolean   normalOreHandling;
	public static String[]  defaultToolsStringArray;
	public static String[]  ignoreBlocksStringArray;
	public static final Boolean BOLD = true;
	public static final int EXHAUSTION_OFF   = 0;
	public static final int EXHAUSTION_DEPTH = 1;
	public static final int EXHAUSTION_FIXED = 2;

    // for this mod- default color is green.
	// support for any color chattext
	public static void sendChat(Player p, String chatMessage) {
		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN)));
		p.sendMessage(component, p.getUUID());
	}


	// support for any color chattext
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {
		TextComponent component = new TextComponent (chatMessage);
		component.setStyle( component.getStyle().withColor(TextColor.fromLegacyFormat(textColor)));
		p.sendMessage(component, p.getUUID());
	}
	
	// support for any color, optionally bold text.
	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor, boolean boldText) {
		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withBold(boldText).withColor(TextColor.fromLegacyFormat(textColor)));
		p.sendMessage(component, p.getUUID());
	}
	
	// support for debug messages
	public static void dbgPrintln(int dbgLevel, String dbgMsg) {
		if (dbgLevel <= debugLevel) {
			System.out.println (dbgMsg);
		}
	}

	public static void dbgPrintln(int dbgLevel, Player p, String dbgMsg ) {
		if (dbgLevel <= debugLevel ) {
			sendChat (p, dbgMsg);	
		}
	}
	
	public static void bakeConfig()
	{
		exhaustionType = COMMON.exhaustionType.get();
		debugLevel = COMMON.debugLevel.get();
		downModifier = COMMON.downModifier.get();
		normalOreHandling = COMMON.normalOre.get();		
		defaultToolsStringArray = extract(COMMON.toolsList.get());
		ToolManager.initTools();
		defaultToolsStringArray = extract(COMMON.toolsList.get());
		ignoreBlocksStringArray = extract(COMMON.ignoreBlocksList.get());
		IgnoreBlocksListManager.initIgnoreBlocksList();

	}

	private static String[] extract(List<? extends String> value)
	{
		return value.toArray(new String[value.size()]);
	}
	
	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}

	public static void pushValues() {
		System.out.println("dbgL:"+MyConfig.debugLevel
						 +" exhT:"+MyConfig.exhaustionType
						 +" DwnSM:" + MyConfig.downModifier
						 +" NmOre:" + MyConfig.normalOreHandling );
		int tdl = MyConfig.debugLevel;
		int ext = MyConfig.exhaustionType;
		int dwm = MyConfig.downModifier;
		boolean noh = MyConfig.normalOreHandling;

		COMMON.debugLevel.set( tdl );
		COMMON.exhaustionType.set( ext );
		COMMON.downModifier.set( dwm );
		COMMON.normalOre.set( noh );

	}

}

