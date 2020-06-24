package com.mactso.hbm.config;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class toolManager {
	public static Hashtable<String, toolItem> toolHashtable = new Hashtable<>();
	private static String defaultToolString = "hbm:default";
	private static int defaultToolDimension = 0;
	private static String defaultToolKey = defaultToolString+":" + defaultToolDimension;
	
	public static toolItem getToolInfo(String key, int dim) {
		String iKey = key + ":" + dim;
		toolItem t = toolHashtable.get(iKey);
		if (t==null) {
			iKey = key + ":" + "0";
			t = toolHashtable.get(iKey);
			if (t==null) {
				t = toolHashtable.get(defaultToolKey);
			}
		}
		return t;
	}
	
	public static void toolInit () {
		int i = 0;
		toolHashtable.clear();
		while (i < MyConfig.aDefaultTools.length) {
			try {
				StringTokenizer st = new StringTokenizer(MyConfig.aDefaultTools[i], ",");
				String key = st.nextToken() + ":" + st.nextToken();
				double tExhaustionY = Double.parseDouble(st.nextToken());
				if ((tExhaustionY < 5.0) || (tExhaustionY > 255.0)) {
					tExhaustionY = 48.0;
				}
				double tExhaustionAmt = Double.parseDouble(st.nextToken());
				if ((tExhaustionAmt < 0.0) || (tExhaustionAmt > 40.0)) {
					tExhaustionAmt = .01;
				}

				toolHashtable.put(key, new toolItem (tExhaustionY, tExhaustionAmt));
			}
			catch (Exception e) {
				System.out.println("HarderBranchMining: Bad Tool Config : " + MyConfig.aDefaultTools[i]);
			}
			i++;
		}
		
		if (getToolInfo(defaultToolString, defaultToolDimension) == null) {
			double tExhaustionY = 48.0;
			double tExhaustionAmt = 10.0;
			toolHashtable.put(defaultToolKey, new toolItem (tExhaustionY, tExhaustionAmt));
		}

	}
	
	public static class toolItem {
		double toolExhaustionY;
		double toolExhaustionAmount;
		public toolItem (double toolExhaustionY, double toolExhaustionAmount) {
			this.toolExhaustionY = toolExhaustionY;
			this.toolExhaustionAmount = toolExhaustionAmount;
		}
		/*
		 *  "Y value where exhaustion begins to apply"
		 */
		public double getExhaustionY () {
			return toolExhaustionY;
		} 
		/*
		 * A value from 0 to 40 of bonus exhaustion.  8.0 =~ 1 food bar per block;
		 */
		public double getExhaustionAmt () {
			return toolExhaustionAmount;
		}
	 
	}
}
