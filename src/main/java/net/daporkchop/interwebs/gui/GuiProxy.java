package net.daporkchop.interwebs.gui;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.NonNull;
import net.daporkchop.interwebs.gui.terminal.TerminalContainer;
import net.daporkchop.interwebs.gui.terminal.TerminalGUI;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * @author DaPorkchop_
 */
public class GuiProxy implements IGuiHandler {
    public static final int ID_TERMINAL = 1;

    private static final Int2ObjectMap<BiFunction<IInventory, TileEntity, Container>> SERVER = new Int2ObjectArrayMap<BiFunction<IInventory, TileEntity, Container>>() {
        {
            this.put(ID_TERMINAL, TerminalContainer::new);
        }

        @SuppressWarnings("unchecked")
        private <T extends TileEntity> void put(int i, @NonNull BiFunction<IInventory, T, Container> func) {
            super.put(i, (BiFunction<IInventory, TileEntity, Container>) func);
        }
    };

    private static final Int2ObjectMap<ClientElementGetter> CLIENT = new Int2ObjectArrayMap<ClientElementGetter>() {
        {
            this.put(ID_TERMINAL, TerminalGUI::new);
        }

        private <T extends TileEntity, C extends Container> void put(int i, @NonNull ClientElementGetter.Simple<T, C> func)  {
            super.put(i, func);
        }
    };

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BiFunction<IInventory, TileEntity, Container> func = SERVER.get(ID);
        if (func == null) {
            return null;
        } else {
            return func.apply(player.inventory, world.getTileEntity(new BlockPos(x, y, z)));
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ClientElementGetter func = CLIENT.get(ID);
        if (func == null) {
            return null;
        } else {
            return func.get(ID, player, world, x, y, z);
        }
    }

    private interface ClientElementGetter   {
        Gui get(int ID, @NonNull EntityPlayer player, @NonNull World world, int x, int y, int z);

        interface Simple<T extends TileEntity, C extends Container> extends ClientElementGetter    {
            @Override
            @SuppressWarnings("unchecked")
            default Gui get(int ID, @NonNull EntityPlayer player, @NonNull World world, int x, int y, int z) {
                T tileEntity = (T) world.getTileEntity(new BlockPos(x, y, z));
                C container = (C) SERVER.get(ID).apply(player.inventory, tileEntity);
                return this.get(tileEntity, container);
            }

            Gui get(@NonNull T tileEntity, @NonNull C container);
        }
    }
}
