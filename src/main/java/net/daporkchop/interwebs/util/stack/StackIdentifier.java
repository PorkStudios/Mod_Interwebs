package net.daporkchop.interwebs.util.stack;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@Getter
public class StackIdentifier {
    private final Item item;
    private final int meta;
    private final NBTTagCompound nbt;

    public static StackIdentifier of(@NonNull ItemStack stack)  {
        return new StackIdentifier(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
    }

    public StackIdentifier(@NonNull Block blockIn) {
        this(blockIn, 0);
    }

    public StackIdentifier(@NonNull Block blockIn, int meta) {
        this(Item.getItemFromBlock(blockIn), meta);
    }

    public StackIdentifier(@NonNull Item itemIn) {
        this(itemIn, 0);
    }

    public StackIdentifier(@NonNull Item itemIn, int meta) {
        this(itemIn, meta, null);
    }

    public StackIdentifier(@NonNull Item itemIn, int meta, NBTTagCompound nbt) {
        this.nbt = nbt;
        this.item = itemIn;
        if (meta < 0) {
            this.meta = 0;
        } else {
            this.meta = meta;
        }
    }

    @Override
    public int hashCode() {
        return (Objects.hashCode(this.item.getRegistryName()) * 127 + this.meta) * 127 + Objects.hashCode(this.nbt);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof StackIdentifier) {
            StackIdentifier other = (StackIdentifier) obj;
            return this.item.equals(other.item) && this.meta == other.meta && Objects.equals(this.nbt, other.nbt);
        } else if (obj instanceof ItemStack) {
            ItemStack other = (ItemStack) obj;
            return this.item.equals(other.getItem()) && this.meta == other.getItemDamage() && Objects.equals(this.nbt, other.getTagCompound());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.nbt == null)   {
            return String.format("%s:%d", this.item.getRegistryName(), this.meta);
        } else {
            return String.format("%s:%d %s", this.item.getRegistryName(), this.meta, this.nbt);
        }
    }
}
