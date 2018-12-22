package net.daporkchop.interwebs.block;

import net.daporkchop.interwebs.ModInterwebs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

/**
 * @author DaPorkchop_
 */
@GameRegistry.ObjectHolder(ModInterwebs.MOD_ID)
public class InterwebsBlocks {
    public static final BlockTerminal terminal = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        try {
            for (Field field : InterwebsBlocks.class.getDeclaredFields())    {
                if (PorkBlock.class.isAssignableFrom(field.getType()))  {
                    ((PorkBlock) field.get(null)).initModel();
                }
            }
        } catch (IllegalAccessException e)  {
            throw new RuntimeException(e);
        }
    }
}
