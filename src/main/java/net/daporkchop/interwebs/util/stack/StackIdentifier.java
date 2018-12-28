/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.interwebs.util.stack;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public static StackIdentifier read(@NonNull ByteBuf buf)    {
        try (DataIn in = NettyByteBufUtil.wrapIn(buf))  {
            return read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static StackIdentifier read(@NonNull DataIn in) throws IOException   {
        NBTTagCompound nbt = in.readBoolean() ? CompressedStreamTools.read(new DataInputStream(in)) : null;
        int meta = in.readInt();
        Item item = Item.getByNameOrId(in.readUTF());
        return item == null ? null : new StackIdentifier(item, meta, nbt);
    }

    public void write(@NonNull ByteBuf buf) {
        try (DataOut out = NettyByteBufUtil.wrapOut(buf))   {
            this.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(@NonNull DataOut out) throws IOException {
        out.writeBoolean(this.nbt != null);
        if (this.nbt != null)   {
            CompressedStreamTools.write(this.nbt, new DataOutputStream(out));
        }
        out.writeInt(this.meta);
        out.writeUTF(this.item.getRegistryName().toString());
    }

    public ItemStack getAsItemStack()   {
        return new ItemStack(this.item, 1, this.meta, this.nbt);
    }

    @Override
    public int hashCode() {
        return (Objects.hashCode(this.item.getRegistryName()) * 1574232431 + this.meta) * 2076444119 + Objects.hashCode(this.nbt);
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
            return String.format("%s:%d(%s)", this.item.getRegistryName(), this.meta, this.nbt);
        }
    }
}
