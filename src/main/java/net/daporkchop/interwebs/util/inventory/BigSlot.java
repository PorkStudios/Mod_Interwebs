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

package net.daporkchop.interwebs.util.inventory;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.interwebs.gui.GuiConstants;
import net.daporkchop.interwebs.interweb.ItemStorage;
import net.daporkchop.interwebs.interweb.inventory.StorageSnapshot;
import net.daporkchop.interwebs.util.stack.BigStack;

/**
 * Like {@link net.minecraft.inventory.Slot} but big
 *
 * @see net.minecraft.inventory.Slot
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Setter
public class BigSlot implements GuiConstants {
    public final int x;
    public final int y;

    public final int index;
    @NonNull
    public final StorageSnapshot snapshot;

    public BigStack getStack(int totalHeight)  {
        return this.snapshot.getStacks()[this.x * totalHeight + this.y];
    }

    public boolean isTouching(int mouseX, int mouseY)  {
        return mouseX >= this.x && mouseX <= this.x + SLOT_WIDTH && mouseY >= this.y && mouseY <= this.y + SLOT_HEIGHT;
    }

    @Override
    public int hashCode() {
        return this.x * 1717915337 + this.y;
    }

    @Override
    public String toString() {
        return String.format("x:%d,y:%d,index:%d,snapshot:%s", this.x, this.y, this.index, this.snapshot);
    }
}
