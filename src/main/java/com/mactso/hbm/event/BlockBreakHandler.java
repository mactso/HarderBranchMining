//1.15.2-2.0.0.3
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.config.ToolManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber()
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
    	
    	double depthBasedExhaustionFactor = 0.0;
    	double tempExhaustionAmount = 0;
       
        PlayerEntity p = event.getPlayer();
        Item item = p.getHeldItemMainhand().getItem();

        // no exhaustion for soft items.
       	if (1 >= event.getState().getBlockHardness(event.getWorld(), event.getPos())) {
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new StringTextComponent ("Block Broken! Soft Block.  No Exhaustion.");
                component.getStyle().setColor(TextFormatting.GREEN);
                p.sendMessage(component);
        	}
       		return;
       	}
       	
       	// normal exhaustion for white list items
       	
        // normal exhaustion for ore blocks
       	Block block = event.getState().getBlock();
       	if (block instanceof OreBlock) {
       		if (MyConfig.aNormalOre) {
       			return;
       		}
       	}

        
        World w = (World) event.getWorld();
        ItemStack tempItemStack = event.getPlayer().getHeldItemMainhand();
        Item tempItem = event.getPlayer().getHeldItemMainhand().getItem();
        
        // domain:tool:dimension
        ToolManager.toolItem toolInfo = ToolManager.getToolInfo(tempItem.getRegistryName().toString(),p.dimension.getId());
         
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
        	System.out.println ("Block Broken! Player:" + p.getName() + ", Dimension"+ p.dimension + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);      
        	if (MyConfig.aDebugLevel > 1) {
                ITextComponent component = 
                		new StringTextComponent ("Block Broken! With "+ tempItem.getRegistryName().toString() 
                				+" by Player:" + p.getName().getFormattedText() 
                				+ ", Dim#:"+ p.dimension.getId() 
                				+ ", Depth:" + event.getPos().getY() 
                				+ ", Exhaustion:" + Math.round(tempExhaustionAmount * 1000.0) / 1000.0);
                component.getStyle().setColor(TextFormatting.GREEN);
                p.sendMessage(component);
        	}
        }
    }
    
	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
    	double depthBasedSpeedFactor = 0.0;

    	
    	String worldName = "server-local ";
    	
    	if(event.getPlayer() == null) {
			return;
		} else if (event.getPlayer().isCreative()) {
			return;
		} 

   		PlayerEntity p = event.getPlayer();
   		if (p.world.isRemote()) {
   			worldName = "client-remote ";
   		}
   		
    	Item playerItem = p.getHeldItemMainhand().getItem();
//    	if (!playerItem.canHarvestBlock(p.getHeldItemMainhand(), event.getState())) {
//    		return;
//    	}
    	boolean toolHarvestsBlockFaster = false;
    	float originalToolSpeed = event.getOriginalSpeed();

    	if (originalToolSpeed > 1.0f) {
    		toolHarvestsBlockFaster = true;
    	}
    	
        // normal mining speed for ore blocks?  default =  true.
       	Block block = event.getState().getBlock();
        // no exhaustion for whitelist items. 
       	// add this in later
       	
        // no exhaustion for ore block items.  
       	if (block instanceof OreBlock) {
       		if (MyConfig.aNormalOre) {
       			return;
       		}
       	}
    	
        Item item = p.getHeldItemMainhand().getItem();

        // no slowdown for soft items.
        if (p.getHeldItemMainhand().isEmpty()) {
        	BlockState s = event.getState();
        	double hardness = s.getBlockHardness(p.world, event.getPos());
        	Block b = event.getState().getBlock();
        	if (hardness <= 1.0) {
        		return;
        	}
        }
    	
        // key = moddomain:tool,dimension

		ToolManager.toolItem toolInfo = 
        		ToolManager.getToolInfo(playerItem.getRegistryName().toString(),
        								p.dimension.getId());

		int altitude = event.getPos().getY();
		if (altitude < 5) {
			altitude = 5;  // cubic chunks compatibility
		}
        // altitude above where tool exhaustion starts.
        if (altitude > toolInfo.getExhaustionY()) {
        	return;
        }
        
        // Speed Factor Detriment Increases with Depth below exhaustion level.
    	depthBasedSpeedFactor = 1.0 -
    				(altitude/toolInfo.getExhaustionY()) ;

    	BlockState s = event.getState();
    	
		// float baseDestroySpeed = playerItem.getDestroySpeed(p.getHeldItemMainhand(), s);
		float baseDestroySpeed = event.getOriginalSpeed();
		float newDestroySpeed = baseDestroySpeed;
		
		if (MyConfig.aDigSpeedModifier>1.0) {
			newDestroySpeed = baseDestroySpeed - baseDestroySpeed * (float) depthBasedSpeedFactor;
			newDestroySpeed = newDestroySpeed / (float) MyConfig.aDigSpeedModifier;
			// Optionally slower digging blocks lower than player feet.
			if (altitude < p.getPosition().getY()) {
				newDestroySpeed = newDestroySpeed / (float) MyConfig.aDownSpeedModifier;
			}
		}
		
		
		if (newDestroySpeed > 0) {
			event.setNewSpeed(newDestroySpeed);
		}
 
		if (MyConfig.aDebugLevel > 0) {
			if (debugLimiter++ > 5) {
				System.out.println("dbgL:"+MyConfig.aDebugLevel
						 +" exT:"+MyConfig.aExhaustionType
						 +" DSM:" + MyConfig.aDigSpeedModifier);
				System.out.println("Block Speed ! depthSpeedFactor:" + (depthBasedSpeedFactor * 100) + "%");
				System.out.println("Block Speed ! Configured digSpeedModifer:" + (MyConfig.aDigSpeedModifier * 100) + "%");
				System.out.println("Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed())+ " DigSpeedMod:"+ MyConfig.aDigSpeedModifier + ".");
				if (MyConfig.aDebugLevel > 1 && p.world.isRemote())  {
		            ITextComponent component = new StringTextComponent (worldName +" Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed()) + "."
		            		+ " DigSpeedMod:"+ MyConfig.aDigSpeedModifier + ".");
		            p.sendMessage(component);
		    	}
				debugLimiter = 0;
			}

		}
	}
    
}

