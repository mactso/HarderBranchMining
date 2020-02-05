 package com.mactso.hbm;

import org.lwjgl.glfw.GLFW;

import com.mactso.hbm.config.MyConfig;
import com.mactso.hbm.event.BlockBreakHandler;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("hbm")
public class Main
{
    public static final String MODID = "hbm"; 

    public Main()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockBreakHandler());
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,MyConfig.SERVER_SPEC );
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
    	@SubscribeEvent
    	public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
    	{
    		// event.getRegistry().register(ModItems.EA_HELMET);

    	}

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onColorsRegistry(final ColorHandlerEvent.Item event)
        {
            // event.getItemColors().register((itemstack, index) -> {
            //	return index > 0 ? -1 : ((IDyeableArmorItem)itemstack.getItem()).getColor(itemstack);
            // }, ModItems.EA_HELMET, ModItems.EA_CHESTPLATE, ModItems.EA_LEGGINGS, ModItems.EA_BOOTS);
        }

        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event)
        {
        	// event.getRegistry().register(EARecipe.CRAFTING_EA);
        }
    }

    @OnlyIn(Dist.CLIENT)
    //@Mod.EventBusSubscriber()
	public static class Events
	{
	    @SubscribeEvent
	    public static void onMouseScreenEvent(GuiScreenEvent.MouseClickedEvent.Pre event)
	    {
	    	if (event.isCanceled())
	    		return;
	    	if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT)
	    		return;
	    }
	}
}
