//1.15.2-2.0
package com.mactso.hbm.config;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

public class ToolManager {
	public static Hashtable<String, toolItem> toolHashtable = new Hashtable<>();
	private static String defaultToolString = "hbm:default";
	private static int defaultToolDimension = 0;
	private static String defaultToolKey = defaultToolString + ":" + defaultToolDimension;

	public static toolItem getToolInfo(String key, int dim) {
		String iKey = key + ":" + dim;

		if (toolHashtable.isEmpty()) {
			toolInit();
		}

		toolItem t = toolHashtable.get(iKey);
		if (t == null) {
			iKey = key + ":" + "0";
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
		String toolLine6464 = "";
		// Issue 6464 patch.
		StringTokenizer st6464 = new StringTokenizer(MyConfig.defaultTools6464, ";");
		while (st6464.hasMoreElements()) {
			toolLine6464 = st6464.nextToken().trim();
			if (toolLine6464.isEmpty()) continue;
			dTL6464.add(toolLine6464);  
			i++;
		}

		MyConfig.defaultTools = dTL6464.toArray(new String[i]);
		
		i = 0;
		toolHashtable.clear();
		while (i < MyConfig.defaultTools.length) {
			try {
				StringTokenizer st = new StringTokenizer(MyConfig.defaultTools[i], ",");
				String modAndTool = st.nextToken();
				String key = modAndTool + ":" + st.nextToken(); //append dimension number.
				
				double tExhaustionY = Double.parseDouble(st.nextToken());
				if ((tExhaustionY < 5.0) || (tExhaustionY > 255.0)) {
					tExhaustionY = 48.0;
				}
				double tExhaustionAmt = Double.parseDouble(st.nextToken());
				if ((tExhaustionAmt < 0.0) || (tExhaustionAmt > 40.0)) {
					tExhaustionAmt = .0;
				}

				toolHashtable.put(key, new toolItem(tExhaustionY, tExhaustionAmt));
				if (!modAndTool.contentEquals("hbm:default") &&
				    !ForgeRegistries.ITEMS.containsKey(new ResourceLocation(modAndTool))
				   )  {
					System.out.println("HarderBranchMining: Tool: " + modAndTool + " not in Forge Registry.  Mispelled?");
				}
			} catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Tool Config : " + MyConfig.defaultTools[i]);
			}
			i++;
		}

		if (getToolInfo(defaultToolString, defaultToolDimension) == null) {
			double tExhaustionY = 48.0;
			double tExhaustionAmt = 10.0;
			toolHashtable.put(defaultToolKey, new toolItem(tExhaustionY, tExhaustionAmt));
		}

	}

	public static class toolItem {
		double toolExhaustionY;
		double toolExhaustionAmount;

		public toolItem(double toolExhaustionY, double toolExhaustionAmount) {
			this.toolExhaustionY = toolExhaustionY;
			this.toolExhaustionAmount = toolExhaustionAmount;
		}

		public double getExhaustionY() {
			return toolExhaustionY;
		}

		public double getExhaustionAmt() {
			return toolExhaustionAmount;
		}

	}

}
