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

package net.daporkchop.interwebs.interweb.inventory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.interwebs.gui.GuiConstants;
import net.daporkchop.interwebs.interweb.ItemStorage;
import net.daporkchop.interwebs.util.stack.BigStack;
import net.daporkchop.interwebs.util.stack.StackIdentifier;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Allows taking snapshots of a storage at a certain point in time
 *
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
public class StorageSnapshot implements GuiConstants {
    private final int width;
    private final int height;
    private final ItemStorage storage;

    private final BigStack[] stacks;

    @Setter
    private int scrollPos;

    private long lastUpdated;

    public StorageSnapshot(@NonNull ItemStorage storage)    {
        this(TERMINAL_SLOTS_WIDTH, TERMINAL_SLOTS_HEIGHT, storage);
    }

    public StorageSnapshot(int width, int height, @NonNull ItemStorage storage)   {
        this.width = width;
        this.height = height;
        this.storage = storage;

        this.stacks = new BigStack[width * height];
    }

    public StorageSnapshot update()    {
        return this.update(System.currentTimeMillis());
    }

    public synchronized StorageSnapshot update(long current)    {
        if (this.lastUpdated < current && this.storage.getLastUpdated() > this.lastUpdated)    {
            this.lastUpdated = current;
            //TODO: we need to optimize this a LOT
            List<Map.Entry<StackIdentifier, AtomicLong>> list = new ArrayList<>(this.storage.getStacks().entrySet());
            int off = this.scrollPos * this.width;
            for (int i = 0; i < this.width * this.height; i++)   {
                int pos = i + off;
                if (pos >= list.size()) {
                    this.stacks[i] = null;
                } else {
                    Map.Entry<StackIdentifier, AtomicLong> entry = list.get(pos);
                    this.stacks[i] = new BigStack(entry.getKey(), entry.getValue());
                }
            }
        }
        return this;
    }

    public StorageSnapshot forEach(@NonNull StackConsumer consumer)   {
        int off = this.scrollPos * this.width;
        for (int x = this.width - 1; x >= 0; x--)   {
            for (int y = this.height - 1; y >= 0; y--)  {
                BigStack stack = this.stacks[x * this.height + y + off];
                if (stack != null)  {
                    consumer.accept(stack, x, y);
                }
            }
        }
        return this;
    }

    @FunctionalInterface
    public interface StackConsumer  {
        void accept(@NonNull BigStack stack, int x, int y);
    }
}
