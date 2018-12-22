package net.daporkchop.interwebs.gui.terminal;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.interwebs.network.inventory.FakeInventory;
import net.daporkchop.interwebs.tile.TileEntityTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * @author DaPorkchop_
 */
@Getter
public class TerminalContainer extends Container {
    private final TileEntityTerminal te;
    public int scroll = 0;

    private final FakeInventory inventory;

    public TerminalContainer(@NonNull IInventory playerInventory, @NonNull TileEntityTerminal te) {
        this.te = te;
        this.inventory = te

        this.addPlayerSlots(playerInventory);
        //add slots for data
    }

    private void addPlayerSlots(@NonNull IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 140;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 58 + 140;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
