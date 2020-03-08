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
	
	public HBMPacket()
	{
	}
	
	public HBMPacket(double aDigSpeedModifier)
	{
		this.aDigSpeedModifier = aDigSpeedModifier;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		aDigSpeedModifier = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(aDigSpeedModifier);
	}
	
	public static class HBMHandler implements IMessageHandler<HBMPacket, IMessage>
	{
		@Override
		public IMessage onMessage(HBMPacket message, MessageContext ctx)
		{
			if (MyConfig.aDebugLevel>0) {
				System.out.println("Message dig: " + message.aDigSpeedModifier);
			}
			Minecraft.getMinecraft().addScheduledTask(() -> {
				MyConfig.serverDigSpeed = message.aDigSpeedModifier;
				MyConfig.aDigSpeedModifier = message.aDigSpeedModifier;				
			});
			return null;	
		}
	}
}
