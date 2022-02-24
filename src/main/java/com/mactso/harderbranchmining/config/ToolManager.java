//1.15.2-2.0
package com.mactso.harderbranchmining.config;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ToolManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static class toolItem {
		int toolYModifierStart;
		int toolYModifierStop;
		double toolDigModifierAmount;
		double toolExhaustionAmount;

		public toolItem(int toolYModifierStart, int toolYModifierStop, 
					 double toolExhaustionAmount, double toolDigModifierAmount) {
			this.toolYModifierStart = toolYModifierStart;
			this.toolYModifierStop = toolYModifierStop;
			this.toolExhaustionAmount = toolExhaustionAmount/100;
			this.toolDigModifierAmount = toolDigModifierAmount/100;

		}


		public int getYModifierStart() {
			return toolYModifierStart;
		}

		public int getYModifierStop() {
			return toolYModifierStop;
		}
		
		public double getExhaustionAmount() {
			return (toolExhaustionAmount);
		}

		public String getExhaustionAmtAsPercent() {
				return String.format("%5.2f%%", 100*toolExhaustionAmount );
		}

		public double getDigModifier() {
			return toolDigModifierAmount;
		}
		
		public String getDigModifierAsPercent () {
			return String.format("%5.2f%%", 100*toolDigModifierAmount );
		}



	}
	public static Hashtable<String, toolItem> toolHashtable = new Hashtable<>();
	private static final ResourceLocation defaultToolString = new ResourceLocation("hbm:default");
	private static final String defaultToolDimensionID = "hbm:default_dimension"; // OVERWORLD

	private static String defaultToolKey = defaultToolString + ":" + defaultToolDimensionID;


	public static toolItem getToolInfo(ResourceLocation key, String dimensionID) {

		if (toolHashtable.isEmpty()) {
			initTools();
		}

		String nameSpace = key.getNamespace();
		String iKey = key + ":" + dimensionID;

		toolItem t = toolHashtable.get(iKey);

		if (t == null) {
			iKey = nameSpace + ":*:" + dimensionID;
			t = toolHashtable.get(iKey);
		}
		
		if (t == null) {
			
			iKey = key + ":" + "minecraft:overworld";
			t = toolHashtable.get(iKey);

		}

		if (t == null) {
			iKey = nameSpace + ":*:minecraft:overworld";
			t = toolHashtable.get(iKey);
		}
		
		if (t == null) {
			t = toolHashtable.get(defaultToolKey);
		}
		
		return t;
	}

	public static void initTools() {
		
		
		int i = 0;
		toolHashtable.clear();
		while (i < MyConfig.defaultToolsStringArray.length) {
			try {
				StringTokenizer st = new StringTokenizer(MyConfig.defaultToolsStringArray[i], ",");
				String modAndTool = st.nextToken();
				String key = modAndTool + ":" + st.nextToken(); // append dimension id.
				
				int toolYModifierStart = Integer.parseInt(st.nextToken());
				if (toolYModifierStart < -2016) {
					toolYModifierStart = -2016;
				}
				if (toolYModifierStart > 255) {
					toolYModifierStart = 255;
				}
				int toolYModifierStop = Integer.parseInt(st.nextToken());
				if (toolYModifierStop < -2016)  {
					toolYModifierStop = -2016;
				}
				if (toolYModifierStop > toolYModifierStart) {
					toolYModifierStop = toolYModifierStart;
				}
				double tExhaustionAmt = Double.parseDouble(st.nextToken());
				if ((tExhaustionAmt <= 0.0) ) {
					tExhaustionAmt = 0.00;
				}
				if (tExhaustionAmt > 100.0) {
					tExhaustionAmt = 100.0;
				}
				double tDigModifier = Double.parseDouble(st.nextToken());
				if ((tDigModifier <= 0.0) ) {
					tDigModifier = 0.00;
				}
				if (tDigModifier > 100.0) {
					tDigModifier = 100.0;
				}
				
				toolHashtable.put(key, new toolItem(toolYModifierStart, toolYModifierStop, tExhaustionAmt, tDigModifier));
				if (!modAndTool.contentEquals("hbm:default1") &&
				    !modAndTool.endsWith(":*") &&
				    !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(modAndTool))
				   )  {
					LOGGER.warn("HarderBranchMining: Tool: " + modAndTool + " not in Forge Registry.  Mispelled?");
				}
			} catch (Exception e) {
				LOGGER.error("HarderBranchMining: Bad Tool Config : " + MyConfig.defaultToolsStringArray[i]);
			}
			i++;
		}

		if (getToolInfo(defaultToolString, defaultToolDimensionID) == null) {

			toolHashtable.put(defaultToolKey, new toolItem(48, 0, 16, 60));
		}

	}

}
