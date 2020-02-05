package com.mactso.hbm.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.hbm.Main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
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

	public static double ExhaustionHeight;
	public static double ExhaustionAmountWoodGold;
	public static double ExhaustionAmountStone;
	public static double ExhaustionAmountIron;
	public static double ExhaustionAmountDiamond;
	public static boolean aBooleanProportionalExhaustion;
	public static boolean aBooleanDebug;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.SERVER_SPEC)
		{
			bakeConfig();
		}
	}

	public static void bakeConfig()
	{
		ExhaustionHeight = SERVER.ExhaustionHeight.get();

		ExhaustionAmountWoodGold = SERVER.ExhaustionAmountWood.get();
		ExhaustionAmountStone = SERVER.ExhaustionAmountStone.get();
		ExhaustionAmountIron = SERVER.ExhaustionAmountIron.get();
		ExhaustionAmountDiamond = SERVER.ExhaustionAmountDiamond.get();
	
		aBooleanProportionalExhaustion = SERVER.aBooleanProportionalExhaustion.get();
		aBooleanDebug = SERVER.aBooleanDebug.get();
		
		System.out.println("HarderBranchMiningConfig: " + ExhaustionHeight + ", " + aBooleanProportionalExhaustion);

	}

	public static class Server
	{
		public final DoubleValue ExhaustionHeight;
		public final DoubleValue ExhaustionAmountWood;
		public final DoubleValue ExhaustionAmountStone;
		public final DoubleValue ExhaustionAmountIron;
		public final DoubleValue ExhaustionAmountDiamond;
		public final BooleanValue aBooleanProportionalExhaustion;
		public final BooleanValue aBooleanDebug;


		public Server(ForgeConfigSpec.Builder builder) {

			builder.push("Exhaustion Height");
			ExhaustionHeight = builder
					.comment("Depth Extra Exhaustion Starts At")
					.translation(Main.MODID + ".config." + "ExhaustionHeight")
					.defineInRange("ExhaustionHeight", () -> 48.0, 5.0, 196.0);

			builder.pop();
			
			aBooleanProportionalExhaustion = builder
					.comment("Exhaustion Proportional to Depth or Flat")
					.translation(Main.MODID + ".config." + "aBooleanProportionalExhaustion")
					.define("aBooleanProportionalExhaustion", false);

			aBooleanDebug = builder
					.comment("Print Debug Message on Block Break")
					.translation(Main.MODID + ".config." + "aBooleanDebug")
					.define("aBooleanDebug", false);

			builder.push("Tool Exhaustion");

			ExhaustionAmountWood = builder
					.comment("Wood/Diamond Tool Exhaustion Amount")
					.translation(Main.MODID + ".config." + "ExhaustionAmountWoodGold")
					.defineInRange("ExhaustionAmountWoodGold", () -> 8.0, 0.1, 10.0);

			ExhaustionAmountStone = builder
					.comment("Stone Tool Exhaustion Amount")
					.translation(Main.MODID + ".config." + "ExhaustionAmountStone")
					.defineInRange("ExhaustionAmountStone", () -> 4.0, 0.1, 10.0);
			
			ExhaustionAmountIron = builder
					.comment("Iron Tool Exhaustion Amount")
					.translation(Main.MODID + ".config." + "ExhaustionAmountIron")
					.defineInRange("ExhaustionAmountIron", () -> 2.0, 0.1, 10.0);

			ExhaustionAmountDiamond= builder
					.comment("Diamond Tool Exhaustion Amount")
					.translation(Main.MODID + ".config." + "ExhaustionAmountDiamond")
					.defineInRange("ExhaustionAmountDiamond", () -> 1.0, 0.1, 10.0);
		
			builder.pop();

		}
	}
}
