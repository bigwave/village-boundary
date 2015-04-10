package org.bigwave.villageboundary;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = VillageBoundaryMod.MODID, version = VillageBoundaryMod.VERSION, dependencies = "", acceptableRemoteVersions = "*")
public class VillageBoundaryMod
{
    public static final String MODID = "villageboundarymod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some events, especially tick, are handled on FML bus
        FMLCommonHandler.instance().bus().register(new WorldTickEventHandler());
    }
}
