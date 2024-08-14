package com.mactso.hbm.command;

import java.util.ArrayList;
import java.util.List;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.manager.ExcludeListManager;
import com.mactso.hbm.manager.IncludeListManager;
import com.mactso.hbm.utility.Utility;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class Commands implements ICommand {

	
	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return "/HarderBranchMining";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return ("Harder Branch Mining Commands.");
	}

	@Override
	public List<String> getAliases() {
		List<String> commandAliases = new ArrayList<>();
		commandAliases.add("harderbranchmining");
		commandAliases.add("/hbm2");
		commandAliases.add("/harderbranchmining");
		return commandAliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayer player = (EntityPlayer) sender;

			if (args[0].equalsIgnoreCase("info")) {
	            showInfo(player);	
			} else if (args[0].equalsIgnoreCase("debug")) {
				if (checkPermission(server, sender)) {
			        changeDebugLevel(args, player);
				}
			}
		}
	}




	private void changeDebugLevel(String[] args, EntityPlayer player) {

		if (args[1] == null) 
			return;
		
		try {
		    	int newDebugLevel = Integer.valueOf(args[1]);
		    		MyConfig.setDebugLevel(newDebugLevel);
		    		Utility.sendDbgChat(0, player, "Debug Value set to : " + MyConfig.getDebugLevel());
			}
			catch (NumberFormatException e){
	    		Utility.sendDbgChat(0, player, "Debug Value must be an integer number. " );
			}
		
	}

	private void showInfo(EntityPlayer player) {
		Utility.sendDbgChat(0, player, "Harder Branch Mining Info");
		Utility.sendDbgChat(0, player, "Current Debuglevel: " + MyConfig.getDebugLevel());
		Utility.sendDbgChat(0, player, "General Digging Modifier: " + (float)(100 * MyConfig.digSpeedModifier -100.0) + " % harder generally");
		Utility.sendDbgChat(0, player, "General Downwards Modifier: " + (float) (100 * MyConfig.downSpeedModifier -100.0) + " % harder downwards");
		Utility.sendDbgChat(0, player, "Exclude List: " + ExcludeListManager.excludelistHashSet.toString());
		Utility.sendDbgChat(0, player, "Include List:" + IncludeListManager.includelistHashSet.toString());
	}

	
	// not using this correctly but it will work.
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender.canUseCommand(3,"debug")) {
			return true;
		}
		return false;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		List<String> commandTabCompletions = new ArrayList<String>();
		commandTabCompletions.add("info");
		commandTabCompletions.add("debug");
		return commandTabCompletions;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}




	}