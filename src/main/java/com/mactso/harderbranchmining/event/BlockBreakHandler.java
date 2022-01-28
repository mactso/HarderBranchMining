//1.15.2-2.0.0.3
package com.mactso.harderbranchmining.event;



import com.mactso.harderbranchmining.config.ManagerBlocksWhiteList;
import com.mactso.harderbranchmining.config.MyConfig;
import com.mactso.harderbranchmining.config.ToolManager;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber()
public class BlockBreakHandler {
	
	private static int debugLimiter = 0;
	
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   

    	if (MyConfig.aExhaustionType == MyConfig.EXHAUSTION_OFF) {
    		if (MyConfig.aDebugLevel > 0) {
				System.out.println("Exhaustion is turned off");
   			}
    		return;
    	}
    	
		if (event.getPlayer()==null) {
			return;
		} else if (event.getPlayer().isCreative()) {
    		return;
    	}
    	
    	double depthBasedExhaustionFactor = 0.0;
    	double tempExhaustionAmount = 0;
       
        Player p = event.getPlayer();
        //Item item = p.getHeldItemMainhand().getItem();

        // no exhaustion for soft items.
        
        float hardness = event.getState().getDestroySpeed(event.getWorld(), event.getPos());
       	if (1 >= event.getState().getDestroySpeed(event.getWorld(), event.getPos())) {
        	if (MyConfig.aDebugLevel > 1) {
				MyConfig.sendChat(p, "Block Broken! Soft Block.  No Exhaustion.");                
        	}
       		return;
       	}
       	

       	Block block = event.getState().getBlock();
        // no exhaustion for whitelist items. 
       	if (ManagerBlocksWhiteList.whitelistHashSet.contains(block)) {
        	if (MyConfig.aDebugLevel > 1) {
        		MyConfig.sendChat(p, "Block Broken! Whitelist Block.  No Exhaustion.");
        	}   
       		return;
       	}
       	
        // normal exhaustion for ore blocks
       	if ((block instanceof RedStoneOreBlock) || (block instanceof OreBlock)) {
       		if (MyConfig.aNormalOreHandling) {
            	if (MyConfig.aDebugLevel > 1) {
            		MyConfig.sendChat(p, "Normal Ore Block broken normally.");
            	}   
       			return;
       		}
       	}

        
        // World debugWorldValue = (World) event.getWorld();
        // ItemStack debugItemStack = event.getPlayer().getHeldItemMainhand();
        Item tempItem = event.getPlayer().getMainHandItem().getItem();
        
        // domain:tool:dimension
        DimensionType dimensionType = p.level.dimensionType();
        ResourceKey<Level> dimensionKey = p.level.dimension();
        String dimensionId = dimensionKey.location().toString();
        ToolManager.toolItem toolInfo = ToolManager.getToolInfo(tempItem.getRegistryName().toString(),dimensionId);
         
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
        
        event.getPlayer().getFoodData().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.aDebugLevel > 0) {
        	System.out.println ("Block Broken! Player:" + p.getName() + ", Dimension:"+ p.level.dimensionType().toString() + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);      
        	if (MyConfig.aDebugLevel > 1) {

        		MyConfig.sendChat (p, "Block Broken! \n With "+ tempItem.getRegistryName().toString() 
                				+" by Player:" + p.getGameProfile().getName() 
                				+ "\n Dimension   :"+ dimensionId
                				+ "\n Depth       :" + event.getPos().getY() 
                				+ "\n Exhaustion:" + Math.round(tempExhaustionAmount * 1000.0) / 1000.0);
        	}
        }
    }

	
	@SubscribeEvent
	public void blockBreakSpeed(PlayerEvent.BreakSpeed event) {
    	double depthBasedSpeedFactor = 0.0;

    	
    	String debugWorldName = "server-local ";
    	
    	if(event.getPlayer() == null) {
			return;
		} else if (event.getPlayer().isCreative()) {
			return;
		} 

   		Player p = event.getPlayer();
   		if (p.level.isClientSide()) {
   			debugWorldName = "client-remote ";
   		}
   		
    	Item playerItem = p.getMainHandItem().getItem();
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
       	if (ManagerBlocksWhiteList.whitelistHashSet.contains(block)) {
        	if (MyConfig.aDebugLevel > 1) {
        		MyConfig.sendChat(p, debugWorldName + ", Breaking Whitelist Block at normal speed.");
        	}   
       		return;
       	}
       	// f230235_a_ == ".contains()"
       	if ((Tags.Blocks.ORES).contains(block)) {
        	if ((MyConfig.aDebugLevel > 1)&&(debugLimiter++ > 39)) {
        		MyConfig.sendChat(p,block.getDescriptionId().toString() + " is in the Ore block tags.");
				debugLimiter = 0;
        	}  
   			return;
       	}
        // no exhaustion for ore block items.  
       	if ((block instanceof RedStoneOreBlock) || (block instanceof OreBlock)) {
       		if (MyConfig.aNormalOreHandling) {
            	if ((MyConfig.aDebugLevel > 1)&&(debugLimiter++ > 39)) {
            		MyConfig.sendChat(p,"Breaking Ore Block full speed with no speed adjust true.");
    				debugLimiter = 0;
            	}   
       			return;
       		}
       	}
    	
        // Item debugItem = p.getHeldItemMainhand().getItem();

        // no slowdown for soft items.
        if (p.getMainHandItem().isEmpty()) {
        	BlockState s = event.getState();
        	double hardness = s.getDestroySpeed(p.level, event.getPos());
        	// Block debugBlock = event.getState().getBlock();
        	if (hardness <= 1.0) {
        		return;
        	}
        }
        
       
        // key = moddomain:tool,dimension
        DimensionType dimensionType = p.level.dimensionType();
        ResourceKey<Level> dimensionKey = p.level.dimension();
        String dimensionId = dimensionKey.location().toString();
        
//        DimensionType dimensionType = p.world.getDimensionType();
//        String dimensionId = dimensionType.getEffects().toString();
        
		ToolManager.toolItem toolInfo = 
        		ToolManager.getToolInfo(playerItem.getRegistryName().toString(),
        								dimensionId);

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

    	// BlockState debugState = event.getState();
    	
		// float baseDestroySpeed = playerItem.getDestroySpeed(p.getHeldItemMainhand(), s);
		float baseDestroySpeed = event.getOriginalSpeed();
		float newDestroySpeed = baseDestroySpeed;
		
		if (MyConfig.aDigSpeedModifier>1.0) {
			newDestroySpeed = baseDestroySpeed - baseDestroySpeed * (float) depthBasedSpeedFactor;
			newDestroySpeed = newDestroySpeed / (float) MyConfig.aDigSpeedModifier;
			// Optionally slower digging blocks lower than player feet.
			if (altitude < p.getY()) {
				newDestroySpeed = newDestroySpeed / (float) MyConfig.aDownSpeedModifier;
			}
		}
		
		
		if (newDestroySpeed > 0) {
			event.setNewSpeed(newDestroySpeed);
		}
 
		if (MyConfig.aDebugLevel > 0) {
			if (debugLimiter++ > 5) {  // debugLimiter to avoid spamming chat window
				System.out.println("dbgL:"+MyConfig.aDebugLevel
						 +" exT:"+MyConfig.aExhaustionType
						 +" DSM:" + MyConfig.aDigSpeedModifier);
				System.out.println("Breaking Block Speed ! depthSpeedFactor:" + (depthBasedSpeedFactor * 100) + "%");
				System.out.println("Breaking Block Speed ! Configured digSpeedModifer: " + (MyConfig.aDigSpeedModifier * 100) + "%");
				System.out.println("Breaking Block Speed ! Original Speed: "+ baseDestroySpeed+ " newSpeedSet:" + (event.getNewSpeed())+ " DigSpeedMod:"+ MyConfig.aDigSpeedModifier + ".");
				if (MyConfig.aDebugLevel > 1 && p.level.isClientSide())  {
					String msg = "\n" + debugWorldName +" :  Breaking Block Speed ! \n Default Minecraft Digging Speed  : "+ baseDestroySpeed 
							+ "\n Standard Digging Speed Modifier .:"+ MyConfig.aDigSpeedModifier 
							+ "\n Modified Breaking Speed           .: " + (event.getNewSpeed()) 
							+ "";
					MyConfig.sendChat (p, msg);
					if (altitude < p.getY ()) {
						msg = " Extra Downward Speed Modifier  .: " + MyConfig.aDownSpeedModifier;
			            MyConfig.sendChat (p, msg, ChatFormatting.YELLOW);
					}
				}
				debugLimiter = 0;
			}

		}
	}

}

