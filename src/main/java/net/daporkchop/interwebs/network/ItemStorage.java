package net.daporkchop.interwebs.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.interwebs.util.StackIdentifier;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
}
