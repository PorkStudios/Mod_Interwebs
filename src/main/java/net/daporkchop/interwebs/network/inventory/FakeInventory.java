package net.daporkchop.interwebs.network.inventory;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.interwebs.network.ItemStorage;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static java.lang.Math.min;

/**
 * @author DaPorkchop_
 */
@Getter
public class FakeInventory extends InventoryBasic {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 6;
    public static final int SLOT_COUNT = WIDTH * HEIGHT;

    private final ItemStorage storage;
    private int scroll;

    public FakeInventory(@NonNull ItemStorage storage) {
        super(storage.getInterweb().getName(), true, SLOT_COUNT);

        this.storage = storage;
    }

    public int scroll(int diff) {
        return this.scroll = min(0, this.scroll + diff);
    }
}
