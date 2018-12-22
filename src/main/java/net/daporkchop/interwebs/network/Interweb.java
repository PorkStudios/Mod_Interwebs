package net.daporkchop.interwebs.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

/**
 * @author DaPorkchop_
 */
@Getter
public class Interweb {
    @NonNull
    @Setter
    private String name;

    private final ItemStorage inventory = new ItemStorage(this);
}
