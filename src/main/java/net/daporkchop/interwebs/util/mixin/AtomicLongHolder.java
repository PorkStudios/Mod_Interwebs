package net.daporkchop.interwebs.util.mixin;

import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public interface AtomicLongHolder {
    AtomicLong getAtomicLong();

    void setAtomicLong(@NonNull AtomicLong countAtomic);
}
