//1.15.2
package com.mactso.hbm.event;

import com.mactso.hbm.config.MyConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Mod.EventBusSubscriber()
public class BlockBreakHandler {
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {   

    	if (event.getPlayer().isCreative()) {
    		return;
    	}
    	
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
        // harvestLevel = event.getPlayer().getHeldItemMainhand().getHarvestLevel(event.getState().getHarvestTool(), event.getPlayer(), event.getState());
        ItemStack tempItemStack = event.getPlayer().getHeldItemMainhand();
        Item tempItem = event.getPlayer().getHeldItemMainhand().getItem();
        ResourceLocation itemid = tempItem.getRegistryName();
        PlayerEntity p = event.getPlayer();
        ItemTier harvestTier = null;
        World w = (World) event.getWorld();
        // Biome b = w.getBiome(getPosition()).getDisplayName().getString();
        int s = w.getSeaLevel();
        if (tempItem instanceof TieredItem) 
        {
        	harvestTier = (ItemTier) ((TieredItem) tempItem).getTier();        	
        }

        
        if (harvestTier == ItemTier.DIAMOND) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountDiamond * depthBasedExhaustionFactor;
		} else if (harvestTier == ItemTier.GOLD) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountGold * depthBasedExhaustionFactor;
		} else if (harvestTier == ItemTier.IRON) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountIron * depthBasedExhaustionFactor;
		} else if (harvestTier == ItemTier.STONE) {
        	tempExhaustionAmount = MyConfig.ExhaustionAmountStone * depthBasedExhaustionFactor;
		} else if (harvestTier == ItemTier.WOOD)  { 
        	tempExhaustionAmount = MyConfig.ExhaustionAmountWood* depthBasedExhaustionFactor;
        } else { // no tool (hand, stick, etc.)
        	tempExhaustionAmount = MyConfig.ExhaustionAmountWood * depthBasedExhaustionFactor * 1.2;
        }
        
        int itemDamage = (int) tempExhaustionAmount;
        if (tempItem.isDamageable()) {
        	tempItemStack.attemptDamageItem(itemDamage, event.getWorld().getRandom(), null);
      //  	tempItem.damageItem(tempItemStack, itemDamage, event.getPlayer(), null);
        }
        event.getPlayer().getFoodStats().addExhaustion((float) tempExhaustionAmount);

        if (MyConfig.aBooleanDebug) {
            // ITextComponent component = new StringTextComponent ("HarderBranchMining! tempExhaustionAmount:" + tempExhaustionAmount);
            // p.sendMessage(component);
        	System.out.println("HarderBranchMining! tempExhaustionAmount:" + tempExhaustionAmount);      
        }
    }
    
}

