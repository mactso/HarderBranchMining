// 1.12.2 version
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mactso.hbm.util.Reference;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BlockBreakHandler {

	@SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   
 
    	if (event.getPlayer().isCreative()) {
    		return;
    	}
    	
    	int harvestLevel = -1;
    	// int tieredHarvestLevel = -1;
    	// int helditem;
    	// double depthFactor = 0.0;
    	double depthBasedExhaustionFactor = 0.0;
    	double tempExhaustionAmount = 0;
    	
        
    	// System.out.println("Block broken! Player Name  :" + event.getPlayer().getName().getFormattedText());
        // System.out.println("Block broken! Player Health:" + event.getPlayer().getHealth());
        // System.out.println("Block broken! Event Y      :" + event.getPos().getY());
        // System.out.println("Block broken! Tool         :" + event.getPlayer().getHeldItemMainhand());

        depthBasedExhaustionFactor = MyConfig.ExhaustionHeight - event.getPos().getY();
        if (depthBasedExhaustionFactor > 0) 
        {
        	if (MyConfig.aBooleanProportionalExhaustion) 
        	{
        		depthBasedExhaustionFactor = depthBasedExhaustionFactor / MyConfig.ExhaustionHeight;
        	} else {
        		depthBasedExhaustionFactor = 1.0;
        	}
        } else 
        {
        	return;
        }
        
         
        // Two paths to getting the harvest level.  The first seemed harder to read so I'm using the second.
        // System.out.println("Block broken! Harvest Level:" + event.getPlayer().getHeldItemMainhand().getHarvestLevel(event.getState().getHarvestTool(), event.getPlayer(), event.getState()));
        System.out.println ("Block Broken! Height=" +  MyConfig.ExhaustionHeight);
        // harvestLevel = event.getPlayer().getHeldItemMainhand().getHarvestLevel(event.getState().getHarvestTool(), event.getPlayer(), event.getState());

        Item item = event.getPlayer().getHeldItemMainhand().getItem();
  
        String itemPickaxeMaterial = "WOOD";
        
        if (item instanceof ItemPickaxe) 
        {
        	ItemPickaxe itemp = (ItemPickaxe) item;
        	itemPickaxeMaterial = itemp.getToolMaterialName();
        	harvestLevel = itemp.getHarvestLevel(null, "pickaxe", null, null);
        	System.out.println ("ToolMaterialName" + itemPickaxeMaterial +"HarvestLevel : " + harvestLevel );
        } 
        
        if (harvestLevel >= 3) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountDiamond * depthBasedExhaustionFactor;
		} else if (harvestLevel == 2) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountIron * depthBasedExhaustionFactor;
		} else if (harvestLevel == 1) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountStone * depthBasedExhaustionFactor;      
		} else if (itemPickaxeMaterial == "GOLD") {
    		tempExhaustionAmount = MyConfig.ExhaustionAmountGold * depthBasedExhaustionFactor;
    	} else if (harvestLevel == 0) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountWood * depthBasedExhaustionFactor;
		} else { // bare hands / wrong tool 
        	tempExhaustionAmount = MyConfig.ExhaustionAmountWood * depthBasedExhaustionFactor * 1.2;
		}
        
        event.getPlayer().getFoodStats().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.aBooleanDebug) {
        System.out.println("Block broken! tempExhaustionAmount:" + tempExhaustionAmount);      
        }
    }
    
}

