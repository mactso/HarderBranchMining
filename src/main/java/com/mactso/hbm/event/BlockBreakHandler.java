// 1.12.2 version
package com.mactso.hbm.event;

import com.google.common.collect.ImmutableMap;
import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.config.toolManager;
import com.mactso.hbm.config.whiteListManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import com.mactso.hbm.util.Reference;
import net.minecraftforge.event.entity.player.PlayerEvent;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BlockBreakHandler {

	private static int debugLimiter = 0;

	@SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   
 
		if (event.getPlayer()==null) {
			return;
		} else if (event.getPlayer().isCreative()) {
    		return;
    	}
    	
    	double depthBasedExhaustionFactor = 0.01;
    	double tempExhaustionAmount = 0;

        EntityPlayer player = event.getPlayer();
    	Item tempItem = player.getHeldItemMainhand().getItem();

        // no exhaustion (extra hunger) - just speed adjustment.
    	
       	if (MyConfig.aExhaustionType == MyConfig.EXHAUSTION_DISABLED) {
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new TextComponentString ("Exhaustion (Hunger) disabled");
                component.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(component);
        	}
       		return;
       	}    	
    	
        // no exhaustion for soft items.
       	if (1 >= event.getState().getBlockHardness(event.getWorld(), event.getPos())) {
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new TextComponentString ("Block Broken! Soft Block.  No Exhaustion.");
                component.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(component);
        	}
       		return;
       	}

       	// Ignore exhaustion/slowdown for ore?
       	Block block = event.getState().getBlock();
       	
       	// Normal ore with no exhaustion (to encourage caving)
       	if ((block instanceof BlockRedstoneOre) ||(block instanceof BlockOre)) {
       		if (MyConfig.aNormalOreHandling) {
            	if (MyConfig.aDebugLevel > 1) {
                    ITextComponent component = 
                    		new TextComponentString ("Normal Ore with Normal Ore Handling Break: " + block.toString() +".  No exhaustion");
                    component.getStyle().setColor(TextFormatting.GREEN);
                    player.sendMessage(component);
            	}
       			return;
       		}
       	}
       	
        // no exhaustion for whitelist items.
       	if (whiteListManager.whitelistHashSet.contains(block)) {
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new TextComponentString ("White list Block Broken: " + block.toString() +".  No exhaustion");
                component.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(component);
        	}
       		return;
       	}
    	

//        // Ignore Exhaustion for Ore if selected.
//       	if (MyConfig.aIgnoreOreFlag) && (event.getState().withProperty(, value) getBlockHardness(event.getWorld(), event.getPos())) {
//        	if (MyConfig.aDebugLevel > 1) {
//                ITextComponent component = 
//                		new TextComponentString ("Block Broken! Soft Block.  No Exhaustion.");
//                component.getStyle().setColor(TextFormatting.GREEN);
//                property.sendMessage(component);
//        	}
//       		return;
//       	}       	
//    	
        // domain:tool, dimension
        toolManager.toolItem toolInfo = toolManager.getToolInfo(tempItem.getRegistryName().toString(),player.dimension);
        
        depthBasedExhaustionFactor = toolInfo.getExhaustionY() - event.getPos().getY();

        if (depthBasedExhaustionFactor < 0)
        	return;
        else {
        	if (MyConfig.aExhaustionType == MyConfig.EXHAUSTION_DEPTH) {
        		depthBasedExhaustionFactor = depthBasedExhaustionFactor / toolInfo.getExhaustionY();
        	} else { // EXHAUSTION_FIXED
        		depthBasedExhaustionFactor = 1.0;
        	}
        }

        tempExhaustionAmount = toolInfo.getExhaustionAmt() * depthBasedExhaustionFactor;
        
        event.getPlayer().getFoodStats().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.aDebugLevel > 0) {
        	System.out.println ("Block Broken! Player:" + player.getName() + ", Dimension"+ player.dimension + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);      
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = new TextComponentString ("Block Broken! Player:" + player.getName() + ", Dimension"+ player.dimension + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);
                player.sendMessage(component);
        	}
        }
    }	

	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
    	double depthBasedSpeedFactor = 0.0;
    	
    	if(event.getEntityPlayer() == null) {
			return;
		} else if (event.getEntityPlayer().isCreative()) {
			return;
		} 

   		EntityPlayer player = event.getEntityPlayer();
    	Item playerItem = player.getHeldItemMainhand().getItem();
    	boolean toolHarvestsBlockFaster = false;
    	float os = event.getOriginalSpeed();

    	if (event.getOriginalSpeed() > 1.0f) {
    		toolHarvestsBlockFaster = true;
    	}
    	
//    	if (!playerItem.canHarvestBlock(event.getState(), player.getHeldItemMainhand())) {
//    		return;
//    	}

       	// Ignore slowdown for ore and whitelist blocks?
       	Block block = event.getState().getBlock();
        // no exhaustion for whitelist items.
       	if (whiteListManager.whitelistHashSet.contains(block)) {
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new TextComponentString ("White list Block Breaking: " + block.toString() + " No speed change.");
                component.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(component);
        	}
       		return;
       	}   
       	
       	// Normal ore with no exhaustion (to encourage caving)
       	if ((block instanceof BlockRedstoneOre) ||(block instanceof BlockOre)) {
       		if (MyConfig.aNormalOreHandling) {
       			return;
       		}
       	}
    	
        // key = moddomain:tool,dimension

		toolManager.toolItem toolInfo = 
        		toolManager.getToolInfo(playerItem.getRegistryName().toString(),
        								player.dimension);
 
		// Cubic Chunks patch
		int altitude = event.getPos().getY();
		if (altitude < 5) {
			altitude = 5;
		}

		if (altitude > toolInfo.getExhaustionY()) {
        	return;
        }
        
        // Speed Factor Detriment Increases with Depth below exhaustion level.
    	depthBasedSpeedFactor = 1.0 -
    				(altitude / toolInfo.getExhaustionY()) ;

    	IBlockState s = event.getState();
    	
		// float baseDestroySpeed = playerItem.getDestroySpeed(p.getHeldItemMainhand(), s);
		float baseDestroySpeed = event.getOriginalSpeed();
		float newDestroySpeed = baseDestroySpeed;
		
		if (MyConfig.aDigSpeedModifier>1.0) {
			newDestroySpeed = baseDestroySpeed - baseDestroySpeed * (float) depthBasedSpeedFactor;
			newDestroySpeed = newDestroySpeed / (float) MyConfig.aDigSpeedModifier;
			if (event.getPos().getY() < player.getPosition().getY()) {
				newDestroySpeed = newDestroySpeed / (float) MyConfig.aDownSpeedModifier;
			}
		}	
		
		if (newDestroySpeed > 0) {
			event.setNewSpeed(newDestroySpeed);
		}
 
		if (MyConfig.aDebugLevel > 1) {
			if (debugLimiter++ > 5) {
				System.out.println("Block Speed ! depthSpeedFactor:" + (depthBasedSpeedFactor * 100) + "%");
				System.out.println("Block Speed ! Configured digSpeedModifer:" + (MyConfig.aDigSpeedModifier * 100) + "%");
				System.out.println("Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed())+ "DigSpeedMod:"+ MyConfig.aDigSpeedModifier + ".");
				if (MyConfig.aDebugLevel > 1) {
		            ITextComponent component = new TextComponentString ("Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed()) + ".");
		            player.sendMessage(component);
		    	}
				debugLimiter = 0;
			}

		}
	}
}

