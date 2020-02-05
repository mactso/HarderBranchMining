package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class BlockBreakHandler {
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   
    	int harvestLevel = -1;
    	int tieredHarvestLevel = -1;
    	int helditem;
    	double depthFactor = 0.0;
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
        	depthBasedExhaustionFactor = 0;
        }
        
         
        // Two paths to getting the harvest level.  The first seemed harder to read so I'm using the second.
        // System.out.println("Block broken! Harvest Level:" + event.getPlayer().getHeldItemMainhand().getHarvestLevel(event.getState().getHarvestTool(), event.getPlayer(), event.getState()));
        // harvestLevel = event.getPlayer().getHeldItemMainhand().getHarvestLevel(event.getState().getHarvestTool(), event.getPlayer(), event.getState());

        Item item = event.getPlayer().getHeldItemMainhand().getItem();
        if (item instanceof TieredItem) 
        {
        	harvestLevel = ((TieredItem) item).getTier().getHarvestLevel();

        }
        
        if (harvestLevel <= 0)  {  // wood but also gold... and also no tool (hand, stick, etc.)
        	tempExhaustionAmount = MyConfig.ExhaustionAmountWoodGold * depthBasedExhaustionFactor;
        } else if (harvestLevel == 1) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountStone * depthBasedExhaustionFactor;
		} else if (harvestLevel == 2) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountIron * depthBasedExhaustionFactor;
		} else if (harvestLevel >= 3) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountDiamond * depthBasedExhaustionFactor;
		}

        event.getPlayer().getFoodStats().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.aBooleanDebug) {
        System.out.println("Block broken! tempExhaustionAmount:" + tempExhaustionAmount);      
        }
    }
    
}

