// 1.12.2 version
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.config.toolManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mactso.hbm.util.Reference;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BlockBreakHandler {
	private static final int EXHAUSTION_FIXED = 0;
	private static final int EXHAUSTION_DEPTH = 1;

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
    	String [] myD = MyConfig.defaultTools;
        
       
        Item item = event.getPlayer().getHeldItemMainhand().getItem();
        World w = (World) event.getWorld();
        EntityPlayer p = event.getPlayer();
        ItemStack tempItemStack = event.getPlayer().getHeldItemMainhand();
        Item tempItem = event.getPlayer().getHeldItemMainhand().getItem();
        
        // domain:tool, dimension
        toolManager.toolItem toolInfo = toolManager.getToolInfo(tempItem.getRegistryName().toString(),p.dimension);
        
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
                ITextComponent component = new TextComponentString ("Block Broken! Player:" + p.getName() + ", Dimension"+ p.dimension + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);
                p.sendMessage(component);
        	}
        }
    }
   
}

