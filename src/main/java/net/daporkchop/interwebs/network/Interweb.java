package net.daporkchop.interwebs.network;

import lombok.Getter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

/**
 * @author DaPorkchop_
 */
@Getter
public class Interweb {
    private final ItemStorage inventory = new ItemStorage(this);
}
