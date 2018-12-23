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

package net.daporkchop.interwebs.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.interwebs.util.stack.StackIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Stores the items for an {@link Interweb}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ItemStorage {
    @NonNull
    private final Interweb interweb;
    //TODO: test if synchronizing this would give better performance
    private final Map<StackIdentifier, AtomicLong> stacks = new ConcurrentHashMap<>();
    @Setter
    private volatile boolean dirty = false;

    public int size() {
        return this.stacks.size();
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public AtomicLong getCountA(@NonNull StackIdentifier identifier) {
        return this.stacks.computeIfAbsent(identifier, i -> new AtomicLong());
    }

    public AtomicLong getCountA(@NonNull ItemStack stack) {
        return this.stacks.computeIfAbsent(StackIdentifier.of(stack), s -> new AtomicLong());
    }

    public long getCount(@NonNull StackIdentifier identifier) {
        return this.stacks.get(identifier).get();
    }

    public long getCount(@NonNull ItemStack stack) {
        return this.stacks.get(stack).get();
    }

    public long addItem(@NonNull ItemStack stack) {
        return this.getCountA(stack).addAndGet(stack.getCount());
    }

    public long decr(@NonNull StackIdentifier identifierIn, long amount)  {
        AtomicLong l = this.stacks.computeIfPresent(identifierIn, (identifier, count) -> {
            if (count.addAndGet(amount) <= 0L)  {
                return null;
            } else {
                return count;
            }
        });
        return l == null ? 0L : l.get();
    }

    public void read(@NonNull NBTTagList tag)   {
        this.stacks.clear();
        StreamSupport.stream(tag.spliterator(), false)
                .map(NBTTagCompound.class::cast)
                .map(compound -> new Tuple<>(
                        new StackIdentifier(
                                Item.getByNameOrId(compound.getString("name")),
                                compound.getInteger("meta"),
                                compound.getCompoundTag("nbt")
                        ),
                        new AtomicLong(compound.getLong("count"))
                ))
                .forEach(tuple -> this.stacks.put(tuple.getFirst(), tuple.getSecond()));
    }

    public NBTTagList write(@NonNull NBTTagList list) {
        this.stacks.entrySet().stream()
                .map(entry -> {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString("name", entry.getKey().getItem().getRegistryName().toString());
                    tag.setInteger("meta", entry.getKey().getMeta());
                    if (entry.getKey().getNbt() != null && !entry.getKey().getNbt().isEmpty())    {
                        tag.setTag("nbt", entry.getKey().getNbt());
                    }
                    tag.setLong("count", entry.getValue().get());
                    return tag;
                })
                .forEach(list::appendTag);
        return list;
    }
}
