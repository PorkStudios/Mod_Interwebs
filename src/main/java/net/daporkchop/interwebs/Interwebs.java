package net.daporkchop.interwebs;

import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;

/**
 * @author DaPorkchop_
 */
@Mod(
        modid = Interwebs.MOD_ID,
        name = Interwebs.MOD_NAME,
        version = Interwebs.VERSION
)
public class Interwebs {
    public static final String MOD_ID = "interwebs";
    public static final String MOD_NAME = "The Interwebs";
    public static final String VERSION = "0.0.1";

    @Mod.Instance(MOD_ID)
    public static Interwebs INSTANCE;

    @Mod.EventHandler
    public void preInit(@NonNull FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(@NonNull FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(@NonNull FMLPostInitializationEvent event) {
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        public static void addItems(@NonNull RegistryEvent.Register<Item> event) {
        }

        @SubscribeEvent
        public static void addBlocks(@NonNull RegistryEvent.Register<Block> event) {
        }
    }
}
