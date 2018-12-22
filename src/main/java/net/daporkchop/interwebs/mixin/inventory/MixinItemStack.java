package net.daporkchop.interwebs.mixin.inventory;

import lombok.NonNull;
import net.daporkchop.interwebs.util.mixin.AtomicLongHolder;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack implements AtomicLongHolder {
    private AtomicLong countAtomic;

    @Override
    public AtomicLong getAtomicLong() {
        return this.countAtomic;
    }

    @Override
    public void setAtomicLong(@NonNull AtomicLong countAtomic) {
        this.countAtomic = countAtomic;
    }
}
