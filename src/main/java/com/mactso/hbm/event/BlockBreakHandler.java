// 1.12.2 version
package com.mactso.hbm.event;

import com.google.common.collect.ImmutableMap;
import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.config.toolManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
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
	private static final int EXHAUSTION_FIXED = 0;
	private static final int EXHAUSTION_DEPTH = 1;
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
       	if (block instanceof BlockOre) {
       		if (MyConfig.aIgnoreOreFlag) {
       			return;
       		}
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
        	if (MyConfig.aExhaustionType == EXHAUSTION_DEPTH) {
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

   		EntityPlayer p = event.getEntityPlayer();
    	Item playerItem = p.getHeldItemMainhand().getItem();
    	if (!playerItem.canHarvestBlock(event.getState(), p.getHeldItemMainhand())) {
    		return;
    	}

       	// Ignore exhaustion for ore?
       	Block block = event.getState().getBlock();
       	if (block instanceof BlockOre) {
       		if (MyConfig.aIgnoreOreFlag) {
       			return;
       		}
       	}
    	
        // key = moddomain:tool,dimension

		toolManager.toolItem toolInfo = 
        		toolManager.getToolInfo(playerItem.getRegistryName().toString(),
        								p.dimension);
        
        if (event.getPos().getY() > toolInfo.getExhaustionY()) {
        	return;
        }
        
        // Speed Factor Detriment Increases with Depth below exhaustion level.
    	depthBasedSpeedFactor = 1.0 -
    				(event.getPos().getY()/toolInfo.getExhaustionY()) ;

    	IBlockState s = event.getState();
    	
		// float baseDestroySpeed = playerItem.getDestroySpeed(p.getHeldItemMainhand(), s);
		float baseDestroySpeed = event.getOriginalSpeed();
		float newDestroySpeed = baseDestroySpeed;
		
		if (MyConfig.aDigSpeedModifier>1.0) {
			newDestroySpeed = baseDestroySpeed - baseDestroySpeed * (float) depthBasedSpeedFactor;
			newDestroySpeed = newDestroySpeed / (float) MyConfig.aDigSpeedModifier;
			if (event.getPos().getY() < p.getPosition().getY()) {
				newDestroySpeed = newDestroySpeed / (float) MyConfig.aDownSpeedModifier;
			}
		}	
		
		if (newDestroySpeed > 0) {
			event.setNewSpeed(newDestroySpeed);
		}
 
		if (MyConfig.aDebugLevel > 0) {
			if (debugLimiter++ > 2) {
				System.out.println("Block Speed ! depthSpeedFactor:" + (depthBasedSpeedFactor * 100) + "%");
				System.out.println("Block Speed ! Configured digSpeedModifer:" + (MyConfig.aDigSpeedModifier * 100) + "%");
				System.out.println("Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed())+ "DigSpeedMod:"+ MyConfig.aDigSpeedModifier + ".");
				if (MyConfig.aDebugLevel > 1) {
		            ITextComponent component = new TextComponentString ("Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed()) + ".");
		            p.sendMessage(component);
		    	}
				debugLimiter = 0;
			}

		}
	}
}

