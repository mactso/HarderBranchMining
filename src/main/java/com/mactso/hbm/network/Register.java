// version 1.12.2
package com.mactso.hbm.network;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.network.HBMPacket.HBMHandler;

public class Register {

	public static void initPackets()
	{
		if (MyConfig.aDebugLevel>0) {
			System.out.println ("HBM: Register Client message");
		}
	    Manager.registerClientMessage(HBMPacket.class, HBMHandler.class);
	}
	
}
