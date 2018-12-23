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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.PrimitiveConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class BigStack implements PrimitiveConstants {
    @NonNull
    private final StackIdentifier identifier;

    @NonNull
    private final AtomicLong count;

    public Item getItem() {
        return this.identifier.getItem();
    }

    public int getMeta() {
        return this.identifier.getMeta();
    }

    public NBTTagCompound getNbt() {
        return this.identifier.getNbt();
    }

    @Override
    public int hashCode() {
        return (int) (this.identifier.hashCode() * 2813494353178762259L + this.hash(this.count.get()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof BigStack) {
            BigStack other = (BigStack) obj;
            return this.identifier.equals(other.identifier) && this.count.get() == other.count.get();
        } else {
            return obj instanceof ItemStack && this.identifier.equals(obj); //if it's an itemstack, don't compare counts
        }
    }

    @Override
    public String toString() {
        return String.format("%s x%d", this.identifier, this.count.get());
    }
}
