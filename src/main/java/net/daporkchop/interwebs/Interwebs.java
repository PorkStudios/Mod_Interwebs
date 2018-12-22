package net.daporkchop.interwebs;

import lombok.NonNull;
import net.daporkchop.interwebs.block.BlockTerminal;
import net.daporkchop.interwebs.block.Blocks;
import net.daporkchop.interwebs.gui.GuiProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;

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
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
    }

    @Mod.EventHandler
    public void postInit(@NonNull FMLPostInitializationEvent event) {
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        public static void addItems(@NonNull RegistryEvent.Register<Item> event) throws IllegalAccessException {
            for (Field field : Blocks.class.getDeclaredFields())    {
                if (Block.class.isAssignableFrom(field.getType()))  {
                    Block block = (Block) field.get(null);
                    event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
                }
            }
        }

        @SubscribeEvent
        public static void addBlocks(@NonNull RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    new BlockTerminal()
            );
        }
    }
}
