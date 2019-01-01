/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

import java.util.ArrayList;
import java.util.List;

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

    public long updateQueued;
    public long lastUpdated;

    public StorageSnapshot(@NonNull ItemStorage storage) {
        this(TERMINAL_SLOTS_WIDTH, TERMINAL_SLOTS_HEIGHT, storage);
    }

    public StorageSnapshot(int width, int height, @NonNull ItemStorage storage) {
        this.width = width;
        this.height = height;
        this.storage = storage;

        this.stacks = new BigStack[width * height];
    }

    public synchronized StorageSnapshot update(long time) {
        if (this.lastUpdated < this.updateQueued && time >= this.updateQueued)  {
            this.lastUpdated = time;
            //TODO: we need to optimize this a LOT
            List<BigStack> list = new ArrayList<>(this.storage.getStacks().values());
            list.sort((a, b) -> Long.compare(b.getCount().get(), a.getCount().get()));
            list.removeIf(BigStack::isEmpty);
            int off = this.scrollPos * this.width;
            for (int x = this.width - 1; x >= 0; x--) {
                for (int y = this.height - 1; y >= 0; y--) {
                    int i = x + y * this.width;
                    int pos = i + off;
                    if (pos >= list.size()) {
                        this.stacks[i] = null;
                    } else {
                        this.stacks[i] = list.get(pos);
                    }
                }
            }
        }
        return this;
    }

    public StorageSnapshot forEach(@NonNull StackConsumer consumer) {
        for (int x = this.width - 1; x >= 0; x--) {
            for (int y = this.height - 1; y >= 0; y--) {
                BigStack stack = this.stacks[x + y * this.width];
                if (stack != null) {
                    consumer.accept(stack, x, y);
                }
            }
        }
        return this;
    }

    @FunctionalInterface
    public interface StackConsumer {
        void accept(@NonNull BigStack stack, int x, int y);
    }
}
