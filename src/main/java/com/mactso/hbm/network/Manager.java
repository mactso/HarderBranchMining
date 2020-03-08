package com.mactso.hbm.network;

import com.mactso.hbm.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Manager
{
	private static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
	private static int id = 0;
		
	public static <REQ extends IMessage, REPLY extends IMessage> void registerServerMessage(Class<REQ> msg, Class<? extends IMessageHandler<REQ, REPLY>> handler)
	{
		WRAPPER.registerMessage(handler, msg, id++, Side.SERVER);
	}
	
	public static <REQ extends IMessage, REPLY extends IMessage> void registerClientMessage(Class<REQ> msg, Class<? extends IMessageHandler<REQ, REPLY>> handler)
	{
		WRAPPER.registerMessage(handler, msg, id++, Side.CLIENT);
	}
	
	public static void sendToServer(IMessage msg)
	{
		WRAPPER.sendToServer(msg);
	}
	
	public static void sendToClient(IMessage msg, EntityPlayerMP player)
	{
		WRAPPER.sendTo(msg, player);
	}
}
