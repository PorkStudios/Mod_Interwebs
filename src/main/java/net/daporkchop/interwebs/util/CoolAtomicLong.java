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

package net.daporkchop.interwebs.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongUnaryOperator;

import static net.daporkchop.lib.common.util.PorkUtil.unsafe;

/**
 * @author DaPorkchop_
 */
public class CoolAtomicLong {
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset(CoolAtomicLong.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile long value;
    private final LongConsumer updateFunction;

    public CoolAtomicLong(@NonNull LongConsumer updateFunction, long value) {
        this.value = value;
        this.updateFunction = updateFunction;
    }

    public CoolAtomicLong(@NonNull LongConsumer updateFunction) {
        this.updateFunction = updateFunction;
    }

    public CoolAtomicLong(long value)   {
        this.value = value;
        this.updateFunction = null;
    }

    public CoolAtomicLong() {
        this(0L);
    }

    public final long get() {
        return this.value;
    }

    public final void set(long newValue) {
        this.value = newValue;
        if (this.updateFunction != null)    {
            this.updateFunction.accept(newValue);
        }
    }

    public final long getAndSet(long newValue) {
        long val = unsafe.getAndSetLong(this, valueOffset, newValue);
        if (this.updateFunction != null)    {
            this.updateFunction.accept(newValue);
        }
        return val;
    }

    public final boolean compareAndSet(long expect, long update) {
        boolean val = unsafe.compareAndSwapLong(this, valueOffset, expect, update);
        if (val && this.updateFunction != null) {
            this.updateFunction.accept(update);
        }
        return val;
    }

    public final long getAndIncrement() {
        long val = unsafe.getAndAddLong(this, valueOffset, 1L);
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long getAndDecrement() {
        long val = unsafe.getAndAddLong(this, valueOffset, -1L);
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long getAndAdd(long delta) {
        long val = unsafe.getAndAddLong(this, valueOffset, delta);
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long incrementAndGet() {
        long val = unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long decrementAndGet() {
        long val = unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long addAndGet(long delta) {
        long val = unsafe.getAndAddLong(this, valueOffset, delta) + delta;
        if (this.updateFunction != null)    {
            this.updateFunction.accept(val);
        }
        return val;
    }

    public final long getAndUpdate(@NonNull LongUnaryOperator updateFunction) {
        long prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsLong(prev);
        } while (!compareAndSet(prev, next));
        if (this.updateFunction != null)    {
            this.updateFunction.accept(prev);
        }
        return prev;
    }

    public final long updateAndGet(@NonNull LongUnaryOperator updateFunction) {
        long prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsLong(prev);
        } while (!compareAndSet(prev, next));
        if (this.updateFunction != null)    {
            this.updateFunction.accept(next);
        }
        return next;
    }

    public final long getAndAccumulate(long x, @NonNull LongBinaryOperator accumulatorFunction) {
        long prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsLong(prev, x);
        } while (!compareAndSet(prev, next));
        if (this.updateFunction != null)    {
            this.updateFunction.accept(prev);
        }
        return prev;
    }

    public final long accumulateAndGet(long x, @NonNull LongBinaryOperator accumulatorFunction) {
        long prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsLong(prev, x);
        } while (!compareAndSet(prev, next));
        if (this.updateFunction != null)    {
            this.updateFunction.accept(next);
        }
        return next;
    }
}
