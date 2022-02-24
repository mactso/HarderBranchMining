package com.mactso.harderbranchmining;

import com.mactso.harderbranchmining.config.ToolManager.toolItem;

public class Utility {
	
	public static double calcDepthFactor(int eventY, toolItem toolInfo) {
		double depthFactor;

		if (eventY > toolInfo.getYModifierStart()) {
			return -1;
		}
		
		int yRange = (toolInfo.getYModifierStart() - toolInfo.getYModifierStop());		
		if (yRange < 1) yRange = 1;
		
		if (eventY < toolInfo.getYModifierStop()) {
			eventY = toolInfo.getYModifierStop();
		}
		
		int altitudeFactor = (toolInfo.getYModifierStart() - eventY);

		
		depthFactor = (double) altitudeFactor / yRange ;
		return depthFactor;
	}
	
	public static String formatPercentage (double value) {
		return String.format("%5.2f%%", value * 100);
	}

}
