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

package net.daporkchop.interwebs.interweb;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.interwebs.net.PacketHandler;
import net.daporkchop.interwebs.net.packet.PacketItemData;
import net.daporkchop.interwebs.util.CoolAtomicLong;
import net.daporkchop.interwebs.util.stack.BigStack;
import net.daporkchop.interwebs.util.stack.StackIdentifier;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.primitive.function.bifunction.LongObjectObjectBiFunction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;
import sun.misc.Unsafe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * Stores the items for an {@link Interweb}
 *
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
public class ItemStorage {
    private final Interweb interweb;
    //TODO: test if synchronizing this would give better performance
    private final Map<StackIdentifier, BigStack> stacks = new ConcurrentHashMap<>();

    private final Consumer<BigStack> updateFunction;
    private final Function<StackIdentifier, BigStack> stackSupplier_1;
    private final LongObjectObjectBiFunction<StackIdentifier, BigStack> stackSupplier_2;

    public ItemStorage(@NonNull Interweb interweb)  {
        this.interweb = interweb;

        this.updateFunction = stack -> this.interweb.getTrackingPlayers().forEach(player -> {
            if (!player.world.isRemote)  {
                PacketHandler.INSTANCE.sendToWithoutFlush(new PacketItemData(stack.getCount().get(), stack.getIdentifier(), this.interweb.getUuid()), (EntityPlayerMP) player);
            }
        });

        this.stackSupplier_1 = identifier -> {
            BigStack stack = new BigStack(identifier);
            stack.setCount(new CoolAtomicLong(l -> this.updateFunction.accept(stack)));
            return stack;
        };
        this.stackSupplier_2 = (count, identifier) -> {
            BigStack stack = new BigStack(identifier);
            stack.setCount(new CoolAtomicLong(l -> this.updateFunction.accept(stack), count));
            return stack;
        };
    }

    public int size() {
        return this.stacks.size();
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public void markDirty() {
        this.interweb.markDirty();
    }

    public CoolAtomicLong getCountAtomic(@NonNull StackIdentifier identifier) {
        return this.stacks.computeIfAbsent(identifier, this.stackSupplier_1).getCount();
    }

    public CoolAtomicLong getCountAtomic(@NonNull ItemStack stack) {
        return this.stacks.computeIfAbsent(StackIdentifier.of(stack), this.stackSupplier_1).getCount();
    }

    public long getCount(@NonNull StackIdentifier identifier) {
        return this.stacks.get(identifier).getCount().get();
    }

    public long getCount(@NonNull ItemStack stack) {
        return this.stacks.get(stack).getCount().get();
    }

    public long addItem(@NonNull ItemStack stack) {
        return this.getCountAtomic(stack).addAndGet(stack.getCount());
    }

    public long decr(@NonNull StackIdentifier identifierIn, long amount) {
        BigStack l = this.stacks.computeIfPresent(identifierIn, (identifier, count) -> {
            if (count.getCount().addAndGet(amount) <= 0L) {
                return null;
            } else {
                return count;
            }
        });
        return l == null ? 0L : l.getCount().get();
    }

    public void read(@NonNull NBTTagList tag) {
        this.stacks.clear();
        StreamSupport.stream(tag.spliterator(), false)
                .map(NBTTagCompound.class::cast)
                .map(compound -> this.stackSupplier_2.apply(
                        compound.getLong("count"),
                        new StackIdentifier(
                                Item.getByNameOrId(compound.getString("name")),
                                compound.getInteger("meta"),
                                compound.hasKey("nbt") ? compound.getCompoundTag("nbt") : null
                        )
                ))
                .forEach(stack -> this.stacks.put(stack.getIdentifier(), stack));
    }

    public NBTTagList write(@NonNull NBTTagList list) {
        this.stacks.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(stack -> stack.getCount().get() > 0L && stack.getItem() != Items.AIR)
                .map(stack -> {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString("name", stack.getItem().getRegistryName().toString());
                    tag.setInteger("meta", stack.getMeta());
                    if (stack.getNbt() != null && !stack.getNbt().isEmpty()) {
                        tag.setTag("nbt", stack.getNbt());
                    }
                    tag.setLong("count", stack.getCount().get());
                    return tag;
                })
                .forEach(list::appendTag);
        return list;
    }

    public void cleanup() {
        this.stacks.entrySet().removeIf(entry -> {
            if (entry.getValue().getCount().get() == 0L) {
                this.markDirty();
                return true;
            } else {
                return false;
            }
        });
    }
}
