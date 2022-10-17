package com.mactso.hbm.network;

import com.mactso.hbm.config.MyConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class HBMPacket implements IMessage
{
	private double aDigSpeedModifier;
	private double aDownSpeedModifier;
	
	public HBMPacket()
	{
	}
	
	public HBMPacket(double aNewDigSpeedModifier, double aNewDownSpeedModifier)
	{
		this.aDigSpeedModifier = aNewDigSpeedModifier;
		this.aDownSpeedModifier = aNewDownSpeedModifier;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		aDigSpeedModifier = buf.readDouble();
		aDownSpeedModifier = buf.readDouble();	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(aDigSpeedModifier);
		buf.writeDouble(aDownSpeedModifier);

	}
	
	public static class HBMHandler implements IMessageHandler<HBMPacket, IMessage>
	{
		@Override
		public IMessage onMessage(HBMPacket message, MessageContext ctx)
		{
			if (MyConfig.debugLevel>0) {
				System.out.println("Message dig: " + message.aDigSpeedModifier);
				System.out.println("Message down: " + message.aDownSpeedModifier);
			}
			Minecraft.getMinecraft().addScheduledTask(() -> {
				MyConfig.serverDigSpeed = message.aDigSpeedModifier;
				MyConfig.digSpeedModifier = message.aDigSpeedModifier;				
				MyConfig.serverDownSpeed = message.aDigSpeedModifier;
				MyConfig.downSpeedModifier = message.aDownSpeedModifier;	
			});
			return null;	
		}
	}
}
