//1.15.2-2.0
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.config.ToolManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber()
public class BlockBreakHandler {
	private static final int EXHAUSTION_FIXED = 0;
	private static final int EXHAUSTION_DEPTH = 1;

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   

		if (event.getPlayer()==null) {
			return;
		} else if (event.getPlayer().isCreative()) {
    		return;
    	}
    	
    	double depthBasedExhaustionFactor = 0.0;
    	double tempExhaustionAmount = 0;
       
     
        Item item = event.getPlayer().getHeldItemMainhand().getItem();
        World w = (World) event.getWorld();
        PlayerEntity p = event.getPlayer();
        ItemStack tempItemStack = event.getPlayer().getHeldItemMainhand();
        Item tempItem = event.getPlayer().getHeldItemMainhand().getItem();
        
        // domain:tool:dimension
        ToolManager.toolItem toolInfo = ToolManager.getToolInfo(tempItem.getRegistryName().toString(),p.dimension.getId());
        
        depthBasedExhaustionFactor = toolInfo.getExhaustionY() - event.getPos().getY();

        if (depthBasedExhaustionFactor < 0)
        	return;
        else {
        	if (MyConfig.exhaustionType == EXHAUSTION_DEPTH) {
        		depthBasedExhaustionFactor = depthBasedExhaustionFactor / toolInfo.getExhaustionY();
        	} else { // EXHAUSTION_FIXED
        		depthBasedExhaustionFactor = 1.0;
        	}
        }

        tempExhaustionAmount = toolInfo.getExhaustionAmt() * depthBasedExhaustionFactor;
        
        event.getPlayer().getFoodStats().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.debugLevel > 0) {
        	System.out.println ("Block Broken! Player:" + p.getName() + ", Dimension"+ p.dimension + ", Pos:" + event.getPos() + ", tempExhaustionAmount:" + tempExhaustionAmount);      
        	if (MyConfig.debugLevel > 1) {
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
    
}

