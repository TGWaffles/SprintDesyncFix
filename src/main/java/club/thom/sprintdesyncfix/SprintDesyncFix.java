package club.thom.sprintdesyncfix;

import club.thom.sprintdesyncfix.listeners.EntityMetadataListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = SprintDesyncFix.MOD_ID, version = SprintDesyncFix.VERSION)
public class SprintDesyncFix {
    public static final String MOD_ID = "SprintDesyncFix";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EntityMetadataListener());
    }
}
