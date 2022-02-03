//1.15.2-2.0
package com.mactso.harderbranchmining.config;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ToolManager {
	public static class toolItem {
		int toolYModifierStart;
		double toolModifierAmount;

		public toolItem(int toolYModifierStart, double toolModifierAmount) {
			this.toolYModifierStart = toolYModifierStart;
			this.toolModifierAmount = toolModifierAmount;
		}

		public double getDigModifier() {
			return toolModifierAmount;
		}
		
		public double getExhaustionAmt() {
			return (toolModifierAmount/3.65);
		}

		public int getYModifierStart() {
			return toolYModifierStart;
		}

	}
	public static Hashtable<String, toolItem> toolHashtable = new Hashtable<>();
	private static String defaultToolString = "hbm:default";
	private static String defaultToolDimensionID = "hbm:default_dimension"; // OVERWORLD

	private static String defaultToolKey = defaultToolString + ":" + defaultToolDimensionID;


	public static toolItem getToolInfo(String key, String dimensionID) {
		String iKey = key + ":" + dimensionID;

		if (toolHashtable.isEmpty()) {
			toolInit();
		}

		toolItem t = toolHashtable.get(iKey);
		if (t == null) {
			iKey = key + ":" + "minecraft:overworld";
			t = toolHashtable.get(iKey);
			if (t == null) {
				t = toolHashtable.get(defaultToolKey);
			}
		}

		return t;
	}

	public static void toolInit() {
		
		List <String> dTL6464 = new ArrayList<>();
		
		int i = 0;
		String tmpToolLine6464 = "";
		// Issue 6464 patch.
		StringTokenizer st6464 = new StringTokenizer(MyConfig.aDefaultTools6464, ";");
		while (st6464.hasMoreElements()) {
			tmpToolLine6464 = st6464.nextToken().trim();
			if (tmpToolLine6464.isEmpty()) continue;
			dTL6464.add(tmpToolLine6464);  
			i++;
		}

		MyConfig.aDefaultTools = dTL6464.toArray(new String[i]);
		
		i = 0;
		toolHashtable.clear();
		while (i < MyConfig.aDefaultTools.length) {
			try {
				StringTokenizer st = new StringTokenizer(MyConfig.aDefaultTools[i], ",");
				String modAndTool = st.nextToken();
				String key = modAndTool + ":" + st.nextToken(); // append dimension id.
				
				int toolYModifierStart = Integer.parseInt(st.nextToken());
				if ((toolYModifierStart < 5) || (toolYModifierStart > 255)) {
					toolYModifierStart = 48;
				}
				double tExhaustionAmt = Double.parseDouble(st.nextToken());
				if ((tExhaustionAmt < 0.0) || (tExhaustionAmt > 40.0)) {
					tExhaustionAmt = .0;
				}

				toolHashtable.put(key, new toolItem(toolYModifierStart, tExhaustionAmt));
				if (!modAndTool.contentEquals("hbm:default") &&
				    !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(modAndTool))
				   )  {
					System.out.println("HarderBranchMining: Tool: " + modAndTool + " not in Forge Registry.  Mispelled?");
				}
			} catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Tool Config : " + MyConfig.aDefaultTools[i]);
			}
			i++;
		}

		if (getToolInfo(defaultToolString, defaultToolDimensionID) == null) {
			int toolYModifierStart = 48;
			double tExhaustionAmt = 10.0;
			toolHashtable.put(defaultToolKey, new toolItem(toolYModifierStart, tExhaustionAmt));
		}

	}

}
